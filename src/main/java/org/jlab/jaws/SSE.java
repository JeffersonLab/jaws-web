package org.jlab.jaws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.jlab.jaws.entity.*;
import org.jlab.jaws.eventsource.*;
import org.jlab.jaws.json.*;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Path("/sse")
@WebListener
public class SSE implements ServletContextListener {
    // max 10 concurrent users
    private final ExecutorService exec = Executors.newFixedThreadPool(10);
    private Sse sse;

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.err.println("Attempting to stop executor service");

        exec.shutdownNow();

        try {
            exec.awaitTermination(8, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Timeout while awaiting shutdown");
            e.printStackTrace();
        }
    }

    @Context
    public void setSse(Sse sse) {
        this.sse = sse;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context final SseEventSink sink,
                       @QueryParam("categoryIndex") @DefaultValue("-1") long categoryIndex,
                       @QueryParam("classIndex") @DefaultValue("-1") long classIndex,
                       @QueryParam("instanceIndex") @DefaultValue("-1") long instanceIndex,
                       @QueryParam("locationIndex") @DefaultValue("-1") long locationIndex,
                       @QueryParam("effectiveIndex") @DefaultValue("-1") long effectiveIndex) {
        System.err.println("Proxy connected: " +
                "categoryIndex: " + categoryIndex +
                ", classIndex: " + classIndex +
                ", instanceIndex: " + instanceIndex +
                ", locationIndex: " + locationIndex +
                ", effectiveIndex: " + effectiveIndex);

        exec.execute(new Runnable() {

            @Override
            public void run() {
                final Properties categoryProps = getCategoryProps();
                final Properties classProps = getClassProps();
                final Properties instanceProps = getInstanceProps();
                final Properties locationProps = getLocationProps();
                final Properties effectiveProps = getEffectiveProps();

                try (
                        EventSourceTable<String, String> categoryTable = new EventSourceTable<>(categoryProps, categoryIndex);
                        EventSourceTable<String, AlarmClass> classTable = new EventSourceTable<>(classProps, classIndex);
                        EventSourceTable<String, AlarmInstance> instanceTable = new EventSourceTable<>(instanceProps, instanceIndex);
                        EventSourceTable<String, AlarmLocation> locationTable = new EventSourceTable<>(locationProps, locationIndex);
                        EventSourceTable<String, EffectiveRegistration> effectiveTable = new EventSourceTable<>(effectiveProps, effectiveIndex)
                ) {

                    categoryTable.addListener(new EventSourceListener<String, String>() {
                        @Override
                        public void highWaterOffset() {
                            sink.send(sse.newEvent("category-highwatermark", ""));
                        }

                        @Override
                        public void batch(LinkedHashMap<String, EventSourceRecord<String, String>> records) {
                            sendCategoryRecords(sink, records.values());
                        }

                    });

                    classTable.addListener(new EventSourceListener<String, AlarmClass>() {
                        @Override
                        public void highWaterOffset() {
                            sink.send(sse.newEvent("class-highwatermark", ""));
                        }

                        @Override
                        public void batch(LinkedHashMap<String, EventSourceRecord<String, AlarmClass>> records) {
                            sendClassRecords(sink, records.values());
                        }

                    });

                    instanceTable.addListener(new EventSourceListener<String, AlarmInstance>() {
                        @Override
                        public void highWaterOffset() {
                            sink.send(sse.newEvent("instance-highwatermark", ""));
                        }

                        @Override
                        public void batch(LinkedHashMap<String, EventSourceRecord<String, AlarmInstance>> records) {
                            sendInstanceRecords(sink, records.values());
                        }

                    });

                    locationTable.addListener(new EventSourceListener<String, AlarmLocation>() {
                        @Override
                        public void highWaterOffset() {
                            sink.send(sse.newEvent("location-highwatermark", ""));
                        }

                        @Override
                        public void batch(LinkedHashMap<String, EventSourceRecord<String, AlarmLocation>> records) {
                            sendLocationRecords(sink, records.values());
                        }

                    });

                    effectiveTable.addListener(new EventSourceListener<String, EffectiveRegistration>() {
                        @Override
                        public void highWaterOffset() {
                            sink.send(sse.newEvent("effective-highwatermark", ""));
                        }

                        @Override
                        public void batch(LinkedHashMap<String, EventSourceRecord<String, EffectiveRegistration>> records) {
                            sendEffectiveRecords(sink, records.values());
                        }

                    });

                    categoryTable.start();
                    classTable.start();
                    instanceTable.start();
                    locationTable.start();
                    effectiveTable.start();


                    try {
                        while (!sink.isClosed()) {
                            sink.send(sse.newEvent("ping", ":"));  // Actively check for connection
                            // Looping waiting for client disconnect
                            Thread.sleep(5000);
                        }
                    } catch (InterruptedException e) {
                        System.err.println("SSE Thread interrupted, shutting down");
                        e.printStackTrace();
                    }
                }
                // Client disconnected if here
            }
        });
    }

    private Properties getCategoryProps() {
        final Properties props = new Properties();

        props.put(EventSourceConfig.EVENT_SOURCE_GROUP, "web-admin-gui-" + Instant.now().toString() + "-" + Math.random());
        props.put(EventSourceConfig.EVENT_SOURCE_TOPIC, JaxRSApp.CATEGORIES_TOPIC);
        props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, JaxRSApp.BOOTSTRAP_SERVERS);
        props.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");

        return props;
    }

    private Properties getLocationProps() {
        final Properties props = new Properties();

        final SpecificAvroSerde<AlarmLocation> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put(EventSourceConfig.EVENT_SOURCE_GROUP, "web-admin-gui-" + Instant.now().toString() + "-" + Math.random());
        props.put(EventSourceConfig.EVENT_SOURCE_TOPIC, JaxRSApp.LOCATIONS_TOPIC);
        props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, JaxRSApp.BOOTSTRAP_SERVERS);
        props.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, VALUE_SERDE.deserializer().getClass().getName());

        // Deserializer specific configs
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, JaxRSApp.SCHEMA_REGISTRY);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

        return props;
    }

    private Properties getClassProps() {
        final Properties props = new Properties();

        final SpecificAvroSerde<AlarmClass> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put(EventSourceConfig.EVENT_SOURCE_GROUP, "web-admin-gui-" + Instant.now().toString() + "-" + Math.random());
        props.put(EventSourceConfig.EVENT_SOURCE_TOPIC, JaxRSApp.CLASSES_TOPIC);
        props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, JaxRSApp.BOOTSTRAP_SERVERS);
        props.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, VALUE_SERDE.deserializer().getClass().getName());

        // Deserializer specific configs
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, JaxRSApp.SCHEMA_REGISTRY);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

        return props;
    }

    private Properties getInstanceProps() {
        final Properties props = new Properties();

        final SpecificAvroSerde<AlarmInstance> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put(EventSourceConfig.EVENT_SOURCE_GROUP, "web-admin-gui-" + Instant.now().toString() + "-" + Math.random());
        props.put(EventSourceConfig.EVENT_SOURCE_TOPIC, JaxRSApp.INSTANCES_TOPIC);
        props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, JaxRSApp.BOOTSTRAP_SERVERS);
        props.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, VALUE_SERDE.deserializer().getClass().getName());

        // Deserializer specific configs
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, JaxRSApp.SCHEMA_REGISTRY);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

        return props;
    }

    private Properties getEffectiveProps() {
        final Properties props = new Properties();

        final SpecificAvroSerde<EffectiveRegistration> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put(EventSourceConfig.EVENT_SOURCE_GROUP, "web-admin-gui-" + Instant.now().toString() + "-" + Math.random());
        props.put(EventSourceConfig.EVENT_SOURCE_TOPIC, JaxRSApp.EFFECTIVE_TOPIC);
        props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, JaxRSApp.BOOTSTRAP_SERVERS);
        props.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, VALUE_SERDE.deserializer().getClass().getName());

        // Deserializer specific configs
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, JaxRSApp.SCHEMA_REGISTRY);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

        return props;
    }

    private void sendCategoryRecords(SseEventSink sink, Collection<EventSourceRecord<String, String>> records) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        ObjectMapper mapper = new ObjectMapper();

        for (EventSourceRecord<String, String> record : records) {
            String key = record.getKey();
            String value = record.getValue();

            String jsonValue = null;

            if (value != null) {
                try {
                    jsonValue = mapper.writeValueAsString(value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String recordString = "{\"key\": \"" + key + "\", \"value\": " + jsonValue + ", \"offset\": " + record.getOffset() + "},";

            builder.append(recordString);
        }

        int i = builder.lastIndexOf(",");

        if(i == -1) {
            builder.append("]");
        } else {
            builder.replace(i, i + 1, "]");
        }

        sink.send(sse.newEvent("category", builder.toString()));
    }

    private void sendClassRecords(SseEventSink sink, Collection<EventSourceRecord<String, AlarmClass>> records) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        ObjectMapper mapper = new ObjectMapper();

        mapper.addMixIn(AlarmClass.class, AlarmClassMixin.class);

        for (EventSourceRecord<String, AlarmClass> record : records) {
            String key = record.getKey();
            AlarmClass value = record.getValue();

            String jsonValue = null;

            if (value != null) {
                try {
                    jsonValue = mapper.writeValueAsString(value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String recordString = "{\"key\": \"" + key + "\", \"value\": " + jsonValue + ", \"offset\": " + record.getOffset() + "},";

            builder.append(recordString);
        }

        int i = builder.lastIndexOf(",");

        if(i == -1) {
            builder.append("]");
        } else {
            builder.replace(i, i + 1, "]");
        }

        sink.send(sse.newEvent("class", builder.toString()));
    }

    private void sendInstanceRecords(SseEventSink sink, Collection<EventSourceRecord<String, AlarmInstance>> records) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        ObjectMapper mapper = new ObjectMapper();

        mapper.addMixIn(AlarmInstance.class, AlarmInstanceMixin.class);
        mapper.addMixIn(SimpleProducer.class, SimpleProducerMixin.class);
        mapper.addMixIn(EPICSProducer.class, EPICSProducerMixin.class);
        mapper.addMixIn(CALCProducer.class, CALCProducerMixin.class);

        for (EventSourceRecord<String, AlarmInstance> record : records) {
            String key = record.getKey();
            AlarmInstance value = record.getValue();

            String jsonValue = null;

            if (value != null) {
                try {
                    jsonValue = mapper.writeValueAsString(value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String recordString = "{\"key\": \"" + key + "\", \"value\": " + jsonValue + ", \"offset\": " + record.getOffset() + "},";

            builder.append(recordString);
        }

        int i = builder.lastIndexOf(",");

        if(i == -1) {
            builder.append("]");
        } else {
            builder.replace(i, i + 1, "]");
        }

        sink.send(sse.newEvent("instance", builder.toString()));
    }

    private void sendLocationRecords(SseEventSink sink, Collection<EventSourceRecord<String, AlarmLocation>> records) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        ObjectMapper mapper = new ObjectMapper();

        mapper.addMixIn(AlarmLocation.class, AlarmLocationMixin.class);

        for (EventSourceRecord<String, AlarmLocation> record : records) {
            String key = record.getKey();
            AlarmLocation value = record.getValue();

            String jsonValue = null;

            if (value != null) {
                try {
                    jsonValue = mapper.writeValueAsString(value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String recordString = "{\"key\": \"" + key + "\", \"value\": " + jsonValue + ", \"offset\": " + record.getOffset() + "},";

            builder.append(recordString);
        }

        int i = builder.lastIndexOf(",");

        if(i == -1) {
            builder.append("]");
        } else {
            builder.replace(i, i + 1, "]");
        }

        sink.send(sse.newEvent("location", builder.toString()));
    }

    private void sendEffectiveRecords(SseEventSink sink, Collection<EventSourceRecord<String, EffectiveRegistration>> records) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        ObjectMapper mapper = new ObjectMapper();

        mapper.addMixIn(EffectiveRegistration.class, EffectiveRegistrationMixin.class);
        mapper.addMixIn(AlarmInstance.class, AlarmInstanceMixin.class);
        mapper.addMixIn(SimpleProducer.class, SimpleProducerMixin.class);
        mapper.addMixIn(EPICSProducer.class, EPICSProducerMixin.class);
        mapper.addMixIn(CALCProducer.class, CALCProducerMixin.class);
        mapper.addMixIn(AlarmClass.class, AlarmClassMixin.class);

        for (EventSourceRecord<String, EffectiveRegistration> record : records) {
            String key = record.getKey();
            EffectiveRegistration value = record.getValue();

            String jsonValue = null;

            if (value != null) {
                try {
                    jsonValue = mapper.writeValueAsString(value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String recordString = "{\"key\": \"" + key + "\", \"value\": " + jsonValue + ", \"offset\": " + record.getOffset() + "},";

            builder.append(recordString);
        }

        int i = builder.lastIndexOf(",");

        if(i == -1) {
            builder.append("]");
        } else {
            builder.replace(i, i + 1, "]");
        }

        sink.send(sse.newEvent("effective", builder.toString()));
    }
}

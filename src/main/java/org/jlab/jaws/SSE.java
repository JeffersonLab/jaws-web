package org.jlab.jaws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.jlab.jaws.clients.*;
import org.jlab.jaws.entity.*;
import org.jlab.jaws.json.*;
import org.jlab.kafka.eventsource.*;

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
                final Properties categoryProps = getConsumerProps(categoryIndex);
                final Properties classProps = getConsumerPropsWithRegistry(classIndex);
                final Properties instanceProps = getConsumerPropsWithRegistry(instanceIndex);
                final Properties locationProps = getConsumerPropsWithRegistry(locationIndex);
                final Properties effectiveProps = getConsumerPropsWithRegistry(effectiveIndex);

                try (
                        CategoryConsumer categoryConsumer = new CategoryConsumer(categoryProps);
                        ClassConsumer classConsumer = new ClassConsumer(classProps);
                        InstanceConsumer instanceConsumer = new InstanceConsumer(instanceProps);
                        LocationConsumer locationConsumer = new LocationConsumer(locationProps);
                        EffectiveRegistrationConsumer registrationConsumer = new EffectiveRegistrationConsumer(effectiveProps)
                ) {
                    categoryConsumer.addListener(new EventSourceListener<String, String>() {
                        @Override
                        public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, String>> records) {
                            sink.send(sse.newEvent("category-highwatermark", ""));
                        }

                        @Override
                        public void batch(List<EventSourceRecord<String, String>> records, boolean highWaterReached) {
                            sendCategoryRecords(sink, records);
                        }

                    });

                    classConsumer.addListener(new EventSourceListener<String, AlarmClass>() {
                        @Override
                        public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmClass>> records) {
                            sink.send(sse.newEvent("class-highwatermark", ""));
                        }

                        @Override
                        public void batch(List<EventSourceRecord<String, AlarmClass>> records, boolean highWaterReached) {
                            sendClassRecords(sink, records);
                        }

                    });

                    instanceConsumer.addListener(new EventSourceListener<String, AlarmInstance>() {
                        @Override
                        public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmInstance>> records) {
                            sink.send(sse.newEvent("instance-highwatermark", ""));
                        }

                        @Override
                        public void batch(List<EventSourceRecord<String, AlarmInstance>> records, boolean highWaterReached) {
                            sendInstanceRecords(sink, records);
                        }

                    });

                    locationConsumer.addListener(new EventSourceListener<String, AlarmLocation>() {
                        @Override
                        public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, AlarmLocation>> records) {
                            sink.send(sse.newEvent("location-highwatermark", ""));
                        }

                        @Override
                        public void batch(List<EventSourceRecord<String, AlarmLocation>> records, boolean highWaterReached) {
                            sendLocationRecords(sink, records);
                        }

                    });

                    registrationConsumer.addListener(new EventSourceListener<String, EffectiveRegistration>() {
                        @Override
                        public void highWaterOffset(LinkedHashMap<String, EventSourceRecord<String, EffectiveRegistration>> records) {
                            sink.send(sse.newEvent("effective-highwatermark", ""));
                        }

                        @Override
                        public void batch(List<EventSourceRecord<String, EffectiveRegistration>> records, boolean highWaterReached) {
                            sendEffectiveRecords(sink, records);
                        }

                    });

                    categoryConsumer.start();
                    classConsumer.start();
                    instanceConsumer.start();
                    locationConsumer.start();
                    registrationConsumer.start();


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

    private Properties getConsumerProps(long resumeOffset) {
        final Properties props = new Properties();

        props.put(EventSourceConfig.EVENT_SOURCE_GROUP, "web-admin-gui-" + Instant.now().toString() + "-" + Math.random());
        props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, JaxRSApp.BOOTSTRAP_SERVERS);
        props.put(EventSourceConfig.EVENT_SOURCE_RESUME_OFFSET, resumeOffset);
        props.put(EventSourceConfig.EVENT_SOURCE_COMPACTED_CACHE, false);

        return props;
    }

    private Properties getConsumerPropsWithRegistry(long resumeOffset) {
        final Properties props = getConsumerProps(resumeOffset);

        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, JaxRSApp.SCHEMA_REGISTRY);

        return props;
    }

    private void sendCategoryRecords(SseEventSink sink, List<EventSourceRecord<String, String>> records) {
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

    private void sendClassRecords(SseEventSink sink, List<EventSourceRecord<String, AlarmClass>> records) {
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

    private void sendInstanceRecords(SseEventSink sink, List<EventSourceRecord<String, AlarmInstance>> records) {
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

    private void sendLocationRecords(SseEventSink sink, List<EventSourceRecord<String, AlarmLocation>> records) {
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

    private void sendEffectiveRecords(SseEventSink sink, List<EventSourceRecord<String, EffectiveRegistration>> records) {
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

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

    private final List<Mixin> CLASS_MIXINS = new ArrayList<>();
    private final List<Mixin> INSTANCE_MIXINS = new ArrayList<>();
    private final List<Mixin> LOCATION_MIXINS = new ArrayList<>();
    private final List<Mixin> REGISTRATION_MIXINS = new ArrayList<>();

    {
        CLASS_MIXINS.add(new Mixin(AlarmClass.class, AlarmClassMixin.class));

        INSTANCE_MIXINS.add(new Mixin(AlarmInstance.class, AlarmInstanceMixin.class));
        INSTANCE_MIXINS.add(new Mixin(SimpleProducer.class, SimpleProducerMixin.class));
        INSTANCE_MIXINS.add(new Mixin(EPICSProducer.class, EPICSProducerMixin.class));
        INSTANCE_MIXINS.add(new Mixin(CALCProducer.class, CALCProducerMixin.class));

        LOCATION_MIXINS.add(new Mixin(AlarmLocation.class, AlarmLocationMixin.class));

        REGISTRATION_MIXINS.add(new Mixin(EffectiveRegistration.class, EffectiveRegistrationMixin.class));
        REGISTRATION_MIXINS.add(new Mixin(AlarmInstance.class, AlarmInstanceMixin.class));
        REGISTRATION_MIXINS.add(new Mixin(SimpleProducer.class, SimpleProducerMixin.class));
        REGISTRATION_MIXINS.add(new Mixin(EPICSProducer.class, EPICSProducerMixin.class));
        REGISTRATION_MIXINS.add(new Mixin(CALCProducer.class, CALCProducerMixin.class));
        REGISTRATION_MIXINS.add(new Mixin(AlarmClass.class, AlarmClassMixin.class));
    }

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
                       @QueryParam("registrationIndex") @DefaultValue("-1") long registrationIndex,
                       @QueryParam("classIndex") @DefaultValue("-1") long classIndex,
                       @QueryParam("instanceIndex") @DefaultValue("-1") long instanceIndex,
                       @QueryParam("locationIndex") @DefaultValue("-1") long locationIndex,
                       @QueryParam("categoryIndex") @DefaultValue("-1") long categoryIndex) {
        System.err.println("Proxy connected: " +
                "registrationIndex: " + registrationIndex +
                ", classIndex: " + classIndex +
                ", instanceIndex: " + instanceIndex +
                ", locationIndex: " + locationIndex +
                ", categoryIndex: " + categoryIndex);

        exec.execute(new Runnable() {

            @Override
            public void run() {
                final Properties registrationProps = getConsumerPropsWithRegistry(registrationIndex);
                final Properties classProps = getConsumerPropsWithRegistry(classIndex);
                final Properties instanceProps = getConsumerPropsWithRegistry(instanceIndex);
                final Properties locationProps = getConsumerPropsWithRegistry(locationIndex);
                final Properties categoryProps = getConsumerProps(categoryIndex);

                try (
                        CategoryConsumer categoryConsumer = new CategoryConsumer(categoryProps);
                        ClassConsumer classConsumer = new ClassConsumer(classProps);
                        InstanceConsumer instanceConsumer = new InstanceConsumer(instanceProps);
                        LocationConsumer locationConsumer = new LocationConsumer(locationProps);
                        EffectiveRegistrationConsumer registrationConsumer = new EffectiveRegistrationConsumer(registrationProps)
                ) {
                    categoryConsumer.addListener(createListener(sink, "category", null));
                    classConsumer.addListener(createListener(sink, "class", CLASS_MIXINS));
                    instanceConsumer.addListener(createListener(sink, "instance", INSTANCE_MIXINS));
                    locationConsumer.addListener(createListener(sink, "location", LOCATION_MIXINS));
                    registrationConsumer.addListener(createListener(sink, "registration", REGISTRATION_MIXINS));

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

    private <K, V> EventSourceListener<K, V> createListener(SseEventSink sink, String eventName, List<Mixin> mixins) {
        return new EventSourceListener<K, V>() {
            @Override
            public void highWaterOffset(LinkedHashMap<K, EventSourceRecord<K, V>> records) {
                sink.send(sse.newEvent(eventName + "-highwatermark", ""));
            }

            @Override
            public void batch(List<EventSourceRecord<K, V>> records, boolean highWaterReached) {
                sendRecords(sink, eventName, records, mixins);
            }
        };
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

    class Mixin {
        public Class<?> target;
        public Class<?> mixinSource;

        public Mixin(Class<?> target, Class<?> mixinSource) {
            this.target = target;
            this.mixinSource = mixinSource;
        }
    }

    private <K, V> void sendRecords(SseEventSink sink, String eventName, List<EventSourceRecord<K, V>> records, List<Mixin> mixins) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        ObjectMapper mapper = new ObjectMapper();

        if(mixins != null) {
            for(Mixin m: mixins) {
                mapper.addMixIn(m.target, m.mixinSource);
            }
        }

        for (EventSourceRecord<K, V> record : records) {
            K key = record.getKey();
            V value = record.getValue();

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

        sink.send(sse.newEvent(eventName, builder.toString()));
    }
}

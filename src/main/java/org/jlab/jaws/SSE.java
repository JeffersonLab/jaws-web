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

    private final List<Mixin> ALARM_MIXINS = new ArrayList<>();
    private final List<Mixin> ACTIVATION_MIXINS = new ArrayList<>();
    private final List<Mixin> CLASS_MIXINS = new ArrayList<>();
    private final List<Mixin> INSTANCE_MIXINS = new ArrayList<>();
    private final List<Mixin> LOCATION_MIXINS = new ArrayList<>();
    private final List<Mixin> NOTIFICATION_MIXINS = new ArrayList<>();
    private final List<Mixin> OVERRIDE_MIXINS = new ArrayList<>();
    private final List<Mixin> REGISTRATION_MIXINS = new ArrayList<>();

    private final StringKeyConverter strKeyConv = new StringKeyConverter();

    {
        ACTIVATION_MIXINS.add(new Mixin(AlarmActivationUnion.class, AlarmActivationMixin.class));
        ACTIVATION_MIXINS.add(new Mixin(Activation.class, ActivationMixin.class));
        ACTIVATION_MIXINS.add(new Mixin(NoteActivation.class, NoteActivationMixin.class));
        ACTIVATION_MIXINS.add(new Mixin(EPICSActivation.class, EPICSActivationMixin.class));
        ACTIVATION_MIXINS.add(new Mixin(ChannelErrorActivation.class, ChannelErrorActivationMixin.class));
        ACTIVATION_MIXINS.add(new Mixin(NoActivation.class, NoActivationMixin.class));

        CLASS_MIXINS.add(new Mixin(AlarmClass.class, AlarmClassMixin.class));

        INSTANCE_MIXINS.add(new Mixin(AlarmInstance.class, AlarmInstanceMixin.class));
        INSTANCE_MIXINS.add(new Mixin(Source.class, SourceMixin.class));
        INSTANCE_MIXINS.add(new Mixin(EPICSSource.class, EPICSSourceMixin.class));
        INSTANCE_MIXINS.add(new Mixin(CALCSource.class, CALCSourceMixin.class));

        LOCATION_MIXINS.add(new Mixin(AlarmLocation.class, AlarmLocationMixin.class));

        OVERRIDE_MIXINS.add(new Mixin(AlarmOverrideUnion.class, AlarmOverrideMixin.class));
        OVERRIDE_MIXINS.add(new Mixin(DisabledOverride.class, DisabledOverrideMixin.class));
        OVERRIDE_MIXINS.add(new Mixin(FilteredOverride.class, FilteredOverrideMixin.class));
        OVERRIDE_MIXINS.add(new Mixin(LatchedOverride.class, LatchedOverrideMixin.class));
        OVERRIDE_MIXINS.add(new Mixin(OffDelayedOverride.class, OffDelayedOverrideMixin.class));
        OVERRIDE_MIXINS.add(new Mixin(OnDelayedOverride.class, OnDelayedOverrideMixin.class));
        OVERRIDE_MIXINS.add(new Mixin(MaskedOverride.class, MaskedOverrideMixin.class));
        OVERRIDE_MIXINS.add(new Mixin(ShelvedOverride.class, ShelvedOverrideMixin.class));

        // Effective entities
        NOTIFICATION_MIXINS.add(new Mixin(EffectiveNotification.class, EffectiveNotificationMixin.class));
        NOTIFICATION_MIXINS.add(new Mixin(AlarmOverrideSet.class, AlarmOverrideSetMixin.class));
        NOTIFICATION_MIXINS.addAll(ACTIVATION_MIXINS);
        NOTIFICATION_MIXINS.addAll(OVERRIDE_MIXINS);

        REGISTRATION_MIXINS.add(new Mixin(EffectiveRegistration.class, EffectiveRegistrationMixin.class));
        REGISTRATION_MIXINS.addAll(INSTANCE_MIXINS);
        REGISTRATION_MIXINS.addAll(CLASS_MIXINS);

        ALARM_MIXINS.add(new Mixin(EffectiveAlarm.class, EffectiveAlarmMixin.class));
        ALARM_MIXINS.addAll(NOTIFICATION_MIXINS);
        ALARM_MIXINS.addAll(REGISTRATION_MIXINS);
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

    /**
     * Listen to Server Sent Events associated with JAWS.
     *
     * @param sink The Server Sent Events sink
     * @param entitiesCsv Comma separted values declaring the entities to monitor
     * @param initiallyActiveOnlyStr If "true", then EffectiveAlarm, EffectiveNotification, and Activation records before highwater are only sent if an Active state;  after highwater all records are sent (so inactivation is provided)
     * @param alarmIndex The starting EffectiveAlarm index or -1 to receive all records
     * @param activationIndex The starting AlarmActivation index or -1 to receive all records
     * @param categoryIndex The starting Category index or -1 to receive all records
     * @param classIndex The starting AlarmClass index or -1 to receive all records
     * @param instanceIndex The starting AlarmInstance index or -1 to receive all records
     * @param locationIndex The starting AlarmLocation index or -1 to receive all records
     * @param notificationIndex The starting EffectiveNotification index or -1 to receive all records
     * @param overrideIndex The starting AlarmOverride index or -1 to receive all records
     * @param registrationIndex The starting AlarmRegistration index or -1 to receive all records
     */
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context final SseEventSink sink,
                       @QueryParam("entitiesCsv") @DefaultValue("alarm,activation,category,class,instance,location,notification,override,registration") String entitiesCsv,
                       @QueryParam("initiallyActiveOnly") @DefaultValue("false") String initiallyActiveOnlyStr,
                       @QueryParam("alarmIndex") @DefaultValue("-1") long alarmIndex,
                       @QueryParam("activationIndex") @DefaultValue("-1") long activationIndex,
                       @QueryParam("categoryIndex") @DefaultValue("-1") long categoryIndex,
                       @QueryParam("classIndex") @DefaultValue("-1") long classIndex,
                       @QueryParam("instanceIndex") @DefaultValue("-1") long instanceIndex,
                       @QueryParam("locationIndex") @DefaultValue("-1") long locationIndex,
                       @QueryParam("notificationIndex") @DefaultValue("-1") long notificationIndex,
                       @QueryParam("overrideIndex") @DefaultValue("-1") long overrideIndex,
                       @QueryParam("registrationIndex") @DefaultValue("-1") long registrationIndex) {
        System.err.println("Proxy connected: " +
                "entitiesCsv: (" + entitiesCsv +
                "), initiallyActiveOnly: " + initiallyActiveOnlyStr +
                ", alarmIndex: " + alarmIndex +
                ", activationIndex: " + activationIndex +
                ", categoryIndex: " + categoryIndex +
                ", classIndex: " + classIndex +
                ", instanceIndex: " + instanceIndex +
                ", locationIndex: " + locationIndex +
                ", notificationIndex: " + notificationIndex +
                ", overrideIndex: " + overrideIndex +
                ", registrationIndex: " + registrationIndex);

        final boolean initiallyActiveOnly = ("true".equals(initiallyActiveOnlyStr));

        String[] tokens = entitiesCsv.split(",");
        List<String> entities = Arrays.asList(tokens);

        boolean alarm = entities.contains("alarm");
        boolean activation = entities.contains("activation");
        boolean category = entities.contains("category");
        boolean clazz = entities.contains("class");
        boolean instance = entities.contains("instance");
        boolean location = entities.contains("location");
        boolean notification = entities.contains("notification");
        boolean override = entities.contains("override");
        boolean registration = entities.contains("registration");

        if(!(alarm || activation || category || clazz || instance || location || notification || override || registration)) {
            sink.send(sse.newEvent("error", "entitiesCsv must contain at least one known value"));
            sink.close();
            return;
        }

        exec.execute(new Runnable() {

            @Override
            public void run() {
                final Properties alarmProps = getConsumerPropsWithRegistry(alarmIndex, initiallyActiveOnly);
                final Properties activationProps = getConsumerPropsWithRegistry(activationIndex, initiallyActiveOnly);
                final Properties categoryProps = getConsumerProps(categoryIndex, false);
                final Properties classProps = getConsumerPropsWithRegistry(classIndex, false);
                final Properties instanceProps = getConsumerPropsWithRegistry(instanceIndex, false);
                final Properties locationProps = getConsumerPropsWithRegistry(locationIndex, false);
                final Properties notificationProps = getConsumerPropsWithRegistry(notificationIndex, initiallyActiveOnly);
                final Properties overrideProps = getConsumerPropsWithRegistry(overrideIndex, false);
                final Properties registrationProps = getConsumerPropsWithRegistry(registrationIndex, false);

                try (
                        EffectiveAlarmConsumer alarmConsumer = new EffectiveAlarmConsumer(alarmProps);
                        ActivationConsumer activationConsumer = new ActivationConsumer(activationProps);
                        CategoryConsumer categoryConsumer = new CategoryConsumer(categoryProps);
                        ClassConsumer classConsumer = new ClassConsumer(classProps);
                        InstanceConsumer instanceConsumer = new InstanceConsumer(instanceProps);
                        LocationConsumer locationConsumer = new LocationConsumer(locationProps);
                        EffectiveNotificationConsumer notificationConsumer = new EffectiveNotificationConsumer(notificationProps);
                        OverrideConsumer overrideConsumer = new OverrideConsumer(overrideProps);
                        EffectiveRegistrationConsumer registrationConsumer = new EffectiveRegistrationConsumer(registrationProps)
                ) {
                    EventSourceListener<String, EffectiveAlarm> alarmListener;
                    EventSourceListener<String, EffectiveNotification> notificationListener;
                    EventSourceListener<String, AlarmActivationUnion> activationListener;

                    if(initiallyActiveOnly) {
                        alarmListener = new AlarmIAOListener(sink);
                        notificationListener = new NotificationIAOListener(sink);
                        activationListener = new ActivationIAOListener(sink);
                    } else {
                        alarmListener = new ESListener<>(sink, "alarm", strKeyConv, ALARM_MIXINS);
                        notificationListener = new ESListener<>(sink, "notification", strKeyConv, NOTIFICATION_MIXINS);
                        activationListener = new ESListener<>(sink, "activation", strKeyConv, ACTIVATION_MIXINS);
                    }

                    alarmConsumer.addListener(alarmListener);
                    activationConsumer.addListener(activationListener);
                    categoryConsumer.addListener(new ESListener<>(sink, "category", strKeyConv, null));
                    classConsumer.addListener(new ESListener<>(sink, "class", strKeyConv, CLASS_MIXINS));
                    instanceConsumer.addListener(new ESListener<>(sink, "instance", strKeyConv, INSTANCE_MIXINS));
                    locationConsumer.addListener(new ESListener<>(sink, "location", strKeyConv, LOCATION_MIXINS));
                    notificationConsumer.addListener(notificationListener);
                    overrideConsumer.addListener(new ESListener<>(sink, "override", new OverrideKeyConverter(), OVERRIDE_MIXINS));
                    registrationConsumer.addListener(new ESListener<>(sink, "registration", strKeyConv, REGISTRATION_MIXINS));

                    if (alarm) alarmConsumer.start();
                    if (activation) activationConsumer.start();
                    if (category) categoryConsumer.start();
                    if (clazz) classConsumer.start();
                    if (instance) instanceConsumer.start();
                    if (location) locationConsumer.start();
                    if (notification) notificationConsumer.start();
                    if (override) overrideConsumer.start();
                    if (registration) registrationConsumer.start();

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

    class ESListener<K, V> implements EventSourceListener<K, V> {
        protected final SseEventSink sink;
        protected final String eventName;
        protected final KeyConverter<K> keyConverter;
        protected final List<Mixin> mixins;

        ESListener(SseEventSink sink, String eventName, KeyConverter<K> keyConverter, List<Mixin> mixins) {
            this.sink = sink;
            this.eventName = eventName;
            this.keyConverter = keyConverter;
            this.mixins = mixins;
        }

        @Override
        public void highWaterOffset(LinkedHashMap<K, EventSourceRecord<K, V>> records) {
            sink.send(sse.newEvent(eventName + "-highwatermark", ""));
        }

        @Override
        public void batch(List<EventSourceRecord<K, V>> records, boolean highWaterReached) {
            sendRecords(sink, eventName, records, keyConverter, mixins);
        }
    }

    abstract class ActiveOnlyESListener<K, V> extends ESListener<K, V> {

        ActiveOnlyESListener(SseEventSink sink, String eventName, KeyConverter<K> keyConverter, List<Mixin> mixins) {
            super(sink, eventName, keyConverter, mixins);
        }

        public boolean isActiveState(AlarmState state) {
            return state == AlarmState.Active || state == AlarmState.ActiveLatched || state == AlarmState.ActiveOffDelayed;
        }

        public boolean isActiveUnion(Object union) {
            return union instanceof EPICSActivation || union instanceof NoteActivation || union instanceof Activation;
        }

        public abstract boolean isActive(V value);

        @Override
        public void highWaterOffset(LinkedHashMap<K, EventSourceRecord<K, V>> records) {
            List<EventSourceRecord<K, V>> activeRecords = new ArrayList<>();
            for(EventSourceRecord<K, V> record: records.values()) {
                if(isActive(record.getValue())) {
                    activeRecords.add(record);
                }
            }
            if(!activeRecords.isEmpty()) {
                sendRecords(sink, eventName, activeRecords, keyConverter, mixins);
            }
            sink.send(sse.newEvent(eventName + "-highwatermark", ""));
        }

        @Override
        public void batch(List<EventSourceRecord<K, V>> records, boolean highWaterReached) {
            if(highWaterReached) {
                sendRecords(sink, eventName, records, keyConverter, mixins);
            }
        }
    }

    class AlarmIAOListener extends ActiveOnlyESListener<String, EffectiveAlarm> {

        AlarmIAOListener(SseEventSink sink) {
            super(sink, "alarm", strKeyConv, ALARM_MIXINS);
        }

        @Override
        public boolean isActive(EffectiveAlarm value) {
            return isActiveState(value.getNotification().getState());
        }
    }

    class NotificationIAOListener extends ActiveOnlyESListener<String, EffectiveNotification> {

        NotificationIAOListener(SseEventSink sink) {
            super(sink, "notification", strKeyConv, NOTIFICATION_MIXINS);
        }

        @Override
        public boolean isActive(EffectiveNotification value) {
            return isActiveState(value.getState());
        }
    }

    class ActivationIAOListener extends ActiveOnlyESListener<String, AlarmActivationUnion> {

        ActivationIAOListener(SseEventSink sink) {
            super(sink, "activation", strKeyConv, ACTIVATION_MIXINS);
        }

        @Override
        public boolean isActive(AlarmActivationUnion value) {
            return isActiveUnion(value.getUnion());
        }
    }

    private Properties getConsumerProps(long resumeOffset, boolean compactedCache) {
        final Properties props = new Properties();

        props.put(EventSourceConfig.GROUP_ID_CONFIG, "web-admin-gui-" + Instant.now().toString() + "-" + Math.random());
        props.put(EventSourceConfig.BOOTSTRAP_SERVERS_CONFIG, JaxRSApp.BOOTSTRAP_SERVERS);
        props.put(EventSourceConfig.RESUME_OFFSET_CONFIG, resumeOffset);
        props.put(EventSourceConfig.COMPACTED_CACHE_CONFIG, compactedCache);

        return props;
    }

    private Properties getConsumerPropsWithRegistry(long resumeOffset, boolean compactedCache) {
        final Properties props = getConsumerProps(resumeOffset, compactedCache);

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

    interface KeyConverter<K> {
        public String toString(K key);
    }

    class StringKeyConverter implements KeyConverter<String> {
        public String toString(String key) {
            return key;
        }
    }

    class OverrideKeyConverter implements KeyConverter<AlarmOverrideKey> {

        @Override
        public String toString(AlarmOverrideKey key) {
            return key.getName() + " " + key.getType();
        }
    }

    private <K, V> void sendRecords(SseEventSink sink, String eventName, List<EventSourceRecord<K, V>> records,
                                    KeyConverter<K> keyConverter, List<Mixin> mixins) {
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

            String keyStr = keyConverter.toString(key);
            String jsonValue = null;

            if (value != null) {
                try {
                    jsonValue = mapper.writeValueAsString(value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String recordString = "{\"key\": \"" + keyStr + "\", \"value\": " + jsonValue + ", \"offset\": " + record.getOffset() + "},";

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

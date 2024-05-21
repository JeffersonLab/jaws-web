package org.jlab.jaws.business.util;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.jlab.jaws.presentation.ws.JaxRSApp;
import org.jlab.kafka.eventsource.EventSourceConfig;

import java.time.Instant;
import java.util.Properties;

public class KafkaConfig {
    public static final String BOOTSTRAP_SERVERS = System.getenv("BOOTSTRAP_SERVERS");
    public static final String SCHEMA_REGISTRY = System.getenv("SCHEMA_REGISTRY");

    static {
        System.err.println("Using BOOTSTRAP_SERVERS = " + BOOTSTRAP_SERVERS);
        System.err.println("Using SCHEMA_REGISTRY = " + SCHEMA_REGISTRY);

        if(BOOTSTRAP_SERVERS == null || SCHEMA_REGISTRY == null) {
            throw new ExceptionInInitializerError("BOOTSTRAP_SERVERS and SCHEMA_REGISTRY env must not be null");
        }
    }

    public static Properties getConsumerProps(long resumeOffset, boolean compactedCache) {
        final Properties props = new Properties();

        props.put(EventSourceConfig.GROUP_ID_CONFIG, "web-admin-gui-" + Instant.now().toString() + "-" + Math.random());
        props.put(EventSourceConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.BOOTSTRAP_SERVERS);
        props.put(EventSourceConfig.RESUME_OFFSET_CONFIG, resumeOffset);
        props.put(EventSourceConfig.COMPACTED_CACHE_CONFIG, compactedCache);

        return props;
    }

    public static Properties getConsumerPropsWithRegistry(long resumeOffset, boolean compactedCache) {
        final Properties props = getConsumerProps(resumeOffset, compactedCache);

        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, KafkaConfig.SCHEMA_REGISTRY);

        return props;
    }

    public static Properties getProducerProps() {
        final Properties props = new Properties();

        props.put("bootstrap.servers", KafkaConfig.BOOTSTRAP_SERVERS);

        return props;
    }

    public static Properties getProducerPropsWithRegistry() {
        final Properties props = getProducerProps();

        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, KafkaConfig.SCHEMA_REGISTRY);

        return props;
    }
}

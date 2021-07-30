package org.jlab.jaws;


import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jlab.jaws.entity.AlarmClass;
import org.jlab.jaws.entity.RegisteredAlarm;
import org.jlab.jaws.entity.SimpleProducer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Properties;

@Path("/rest")
public class REST {
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void putRegistration(@FormParam("name") String name, @FormParam("rationale") String rationale)
    {
        System.out.println("PUT received: " + name);

        final String servers = "localhost:9094";
        final String registry = "http://localhost:8081";
        final String registeredTopic = "registered-alarms";

        String key = name;

        RegisteredAlarm value = new RegisteredAlarm();
        value.setClass$(AlarmClass.Base_Class);
        value.setProducer(new SimpleProducer());
        value.setRationale(rationale);

        Properties props = getRegistrationProps(servers, registry);

        try(KafkaProducer<String, RegisteredAlarm> producer = new KafkaProducer<>(props)) {
            producer.send(new ProducerRecord<>(registeredTopic, key, value));
        }
    }


    private Properties getRegistrationProps(String servers, String registry) {
        final Properties props = new Properties();

        final SpecificAvroSerde<RegisteredAlarm> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put("bootstrap.servers", servers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", VALUE_SERDE.serializer().getClass().getName());

        // Serializer specific configs
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, registry);

        return props;
    }
}

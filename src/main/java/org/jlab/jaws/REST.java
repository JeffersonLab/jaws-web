package org.jlab.jaws;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jlab.jaws.entity.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Properties;

@Path("/rest")
public class REST {

    @GET
    @Path("locations")
    @Produces("application/json")
    public String getLocations() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(AlarmLocation.values());
    }

    @GET
    @Path("categories")
    @Produces("application/json")
    public String getCategories() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(AlarmCategory.values());
    }

    @PUT
    @Path("/registered")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void putRegistration(            @FormParam("name") String name,
                                            @FormParam("location") String location,
                                            @FormParam("category") String category,
                                            @FormParam("rationale") String rationale)
    {
        System.out.println("PUT received: " + name);

        final String servers = "localhost:9094";
        final String registry = "http://localhost:8081";
        final String topic = "registered-alarms";

        String key = name;

        RegisteredAlarm value = new RegisteredAlarm();
        value.setClass$(AlarmClass.Base_Class);
        value.setProducer(new SimpleProducer());
        value.setRationale(rationale);


        AlarmLocation al = null;
        if(location != null) {
            al = AlarmLocation.valueOf(location);
        }
        value.setLocation(al);

        AlarmCategory ac = null;

        if(category != null) {
            ac = AlarmCategory.valueOf(category);
        }
        value.setCategory(ac);

        Properties props = getRegisteredProps(servers, registry);

        try(KafkaProducer<String, RegisteredAlarm> producer = new KafkaProducer<>(props)) {
            producer.send(new ProducerRecord<>(topic, key, value));
        }
    }


    private Properties getRegisteredProps(String servers, String registry) {
        final Properties props = new Properties();

        final SpecificAvroSerde<RegisteredAlarm> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put("bootstrap.servers", servers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", VALUE_SERDE.serializer().getClass().getName());

        // Serializer specific configs
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, registry);

        return props;
    }

    @PUT
    @Path("/class")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void putClass(
            @FormParam("name") String name,
            @FormParam("location") String location,
            @FormParam("rationale") String rationale)
    {
        System.out.println("PUT received: " + name);

        final String servers = "localhost:9094";
        final String registry = "http://localhost:8081";
        final String topic = "registered-classes";

        RegisteredClassKey key = new RegisteredClassKey();
        key.setClass$(AlarmClass.Base_Class);

        RegisteredClass value = new RegisteredClass();
        value.setLocation(AlarmLocation.A1);
        value.setCategory(AlarmCategory.BCM);

        Properties props = getClassProps(servers, registry);

        try(KafkaProducer<RegisteredClassKey, RegisteredClass> producer = new KafkaProducer<>(props)) {
            producer.send(new ProducerRecord<>(topic, key, value));
        }
    }

    private Properties getClassProps(String servers, String registry) {
        final Properties props = new Properties();

        final SpecificAvroSerde<RegisteredClassKey> KEY_SERDE = new SpecificAvroSerde<>();
        final SpecificAvroSerde<RegisteredClass> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put("bootstrap.servers", servers);
        props.put("key.serializer", KEY_SERDE.serializer().getClass().getName());
        props.put("value.serializer", VALUE_SERDE.serializer().getClass().getName());

        // Serializer specific configs
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, registry);

        return props;
    }
}

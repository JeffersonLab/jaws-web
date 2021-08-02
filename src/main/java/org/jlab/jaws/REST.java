package org.jlab.jaws;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jlab.jaws.entity.*;

import javax.validation.constraints.NotNull;
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

    @GET
    @Path("classes")
    @Produces("application/json")
    public String getClasses() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(AlarmClass.values());
    }

    @DELETE
    @Path("/registered")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void deleteRegistration(
        @FormParam("name") @NotNull(message = "alarm name is required") String name) {
        System.err.println("Deleting registration: " + name);

        final String servers = "localhost:9094";
        final String registry = "http://localhost:8081";
        final String topic = "registered-alarms";

        String key = name;

        Properties props = getRegisteredProps(servers, registry);

        try(KafkaProducer<String, RegisteredAlarm> p = new KafkaProducer<>(props)) {
            p.send(new ProducerRecord<>(topic, key, null));
        }
    }

    @PUT
    @Path("/registered")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void putRegistration(            @FormParam("name") @NotNull(message = "alarm name is required") String name,
                                            @FormParam("class") @NotNull(message = "class is required") String clazz,
                                            @FormParam("pv") String pv,
                                            @FormParam("expression") String expression,
                                            @FormParam("priority") String priority,
                                            @FormParam("location") String location,
                                            @FormParam("category") String category,
                                            @FormParam("rationale") String rationale,
                                            @FormParam("correctiveaction") String correctiveaction,
                                            @FormParam("pocusername") String pocusername,
                                            @FormParam("filterable") Boolean filterable,
                                            @FormParam("latching") Boolean latching,
                                            @FormParam("maskedby") String maskedby,
                                            @FormParam("ondelayseconds") Long ondelayseconds,
                                            @FormParam("offdelayseconds") Long offdelayseconds,
                                            @FormParam("screenpath") String screenpath)
    {
        System.out.println("PUT received: " + name);

        final String servers = "localhost:9094";
        final String registry = "http://localhost:8081";
        final String topic = "registered-alarms";

        String key = name;

        RegisteredAlarm value = new RegisteredAlarm();


        AlarmClass acl = AlarmClass.Base_Class;
        if(clazz != null) {
            acl = AlarmClass.valueOf(clazz);
        }

        value.setClass$(acl);

        Object producer = new SimpleProducer();

        if(pv != null) {
            producer = new EPICSProducer(pv);
        } else if(expression != null) {
            producer = new CALCProducer(expression);
        }

        value.setProducer(producer);

        value.setRationale(rationale);

        AlarmPriority ap = null;
        if(priority != null) {
            ap = AlarmPriority.valueOf(priority);
        }
        value.setPriority(ap);

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

        value.setCorrectiveaction(correctiveaction);
        value.setPointofcontactusername(pocusername);
        value.setFilterable(filterable);
        value.setLatching(latching);
        value.setMaskedby(maskedby);
        value.setOffdelayseconds(ondelayseconds);
        value.setOndelayseconds(offdelayseconds);
        value.setScreenpath(screenpath);

        Properties props = getRegisteredProps(servers, registry);

        try(KafkaProducer<String, RegisteredAlarm> p = new KafkaProducer<>(props)) {
            p.send(new ProducerRecord<>(topic, key, value));
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
        value.setPriority(AlarmPriority.P3_MINOR);
        value.setScreenpath("/");

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

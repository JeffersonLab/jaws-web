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
    @Path("priorities")
    @Produces("application/json")
    public String getPriorities() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(AlarmPriority.values());
    }


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

    @DELETE
    @Path("/registered")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void deleteRegistration(
        @FormParam("name") @NotNull(message = "alarm name is required") String name) {
        System.err.println("Deleting registration: " + name);

        final String topic = "registered-alarms";

        String key = name;

        Properties props = getRegisteredProps(JaxRSApp.BOOTSTRAP_SERVERS, JaxRSApp.SCHEMA_REGISTRY);

        try(KafkaProducer<String, RegisteredAlarm> p = new KafkaProducer<>(props)) {
            p.send(new ProducerRecord<>(topic, key, null));
        }
    }

    @PUT
    @Path("/registered")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void putRegistration(            @FormParam("name") @NotNull(message = "alarm name is required") String name,
                                            @FormParam("class") @NotNull(message = "class is required") String clazz,
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
                                            @FormParam("screenpath") String screenpath,
                                            @FormParam("epicspv") String epicspv)
    {
        System.out.println("PUT received: " + name);

        final String topic = "registered-alarms";

        String key = name;

        RegisteredAlarm value = new RegisteredAlarm();

        value.setClass$(clazz);

        Object producer = new SimpleProducer();

        if(epicspv != null) {
            producer = new EPICSProducer(epicspv);
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

        Properties props = getRegisteredProps(JaxRSApp.BOOTSTRAP_SERVERS, JaxRSApp.SCHEMA_REGISTRY);

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

    @DELETE
    @Path("/class")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void deleteClass(
            @FormParam("name") @NotNull(message = "class name is required") String name) {
        System.err.println("Deleting class: " + name);

        final String topic = "registered-classes";

        String key = name;

        Properties props = getRegisteredProps(JaxRSApp.BOOTSTRAP_SERVERS, JaxRSApp.SCHEMA_REGISTRY);

        try(KafkaProducer<String, RegisteredClass> p = new KafkaProducer<>(props)) {
            p.send(new ProducerRecord<>(topic, key, null));
        }
    }

    @PUT
    @Path("/class")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void putClass(
            @FormParam("name") @NotNull(message = "class name is required") String name,
            @FormParam("priority") @NotNull(message = "priority is required") String priority,
            @FormParam("location") @NotNull(message = "location is required") String location,
            @FormParam("category") @NotNull(message = "category is required") String category,
            @FormParam("rationale") @NotNull(message = "rationale is required") String rationale,
            @FormParam("correctiveaction") @NotNull(message = "correctiveaction is required") String correctiveaction,
            @FormParam("pocusername") @NotNull(message = "pocusername is required") String pocusername,
            @FormParam("filterable") Boolean filterable,
            @FormParam("latching") Boolean latching,
            @FormParam("maskedby") String maskedby,
            @FormParam("ondelayseconds") Long ondelayseconds,
            @FormParam("offdelayseconds") Long offdelayseconds,
            @FormParam("screenpath") @NotNull(message = "screenpath is required") String screenpath)
    {
        System.out.println("PUT received: " + name);

        final String topic = "registered-classes";

        String key = name;

        RegisteredClass value = new RegisteredClass();

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

        Properties props = getClassProps(JaxRSApp.BOOTSTRAP_SERVERS, JaxRSApp.SCHEMA_REGISTRY);

        try(KafkaProducer<String, RegisteredClass> producer = new KafkaProducer<>(props)) {
            producer.send(new ProducerRecord<>(topic, key, value));
        }
    }

    private Properties getClassProps(String servers, String registry) {
        final Properties props = new Properties();

        final SpecificAvroSerde<RegisteredClass> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put("bootstrap.servers", servers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", VALUE_SERDE.serializer().getClass().getName());

        // Serializer specific configs
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, registry);

        return props;
    }
}

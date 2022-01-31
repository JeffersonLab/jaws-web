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
import java.util.List;
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

    @DELETE
    @Path("/instance")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void deleteRegistration(
        @FormParam("name") @NotNull(message = "alarm name is required") String name) {
        System.err.println("Deleting registration: " + name);

        String key = name;

        Properties props = getInstanceProps(JaxRSApp.BOOTSTRAP_SERVERS, JaxRSApp.SCHEMA_REGISTRY);

        try(KafkaProducer<String, AlarmInstance> p = new KafkaProducer<>(props)) {
            p.send(new ProducerRecord<>(JaxRSApp.INSTANCES_TOPIC, key, null));
        }
    }

    @PUT
    @Path("/instance")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void putInstance(            @FormParam("name") @NotNull(message = "alarm name is required") String name,
                                            @FormParam("class") @NotNull(message = "class is required") String clazz,
                                            @FormParam("expression") String expression,
                                            @FormParam("location") List<String> location,
                                            @FormParam("maskedby") String maskedby,
                                            @FormParam("screencommand") String screencommand,
                                            @FormParam("epicspv") String epicspv)
    {
        System.out.println("PUT received: " + name);

        String key = name;

        AlarmInstance value = new AlarmInstance();

        value.setClass$(clazz);

        Object producer = new SimpleProducer();

        if(epicspv != null) {
            producer = new EPICSProducer(epicspv);
        } else if(expression != null) {
            producer = new CALCProducer(expression);
        }

        value.setProducer(producer);
        value.setLocation(location);
        value.setMaskedby(maskedby);
        value.setScreencommand(screencommand);

        Properties props = getInstanceProps(JaxRSApp.BOOTSTRAP_SERVERS, JaxRSApp.SCHEMA_REGISTRY);

        try(KafkaProducer<String, AlarmInstance> p = new KafkaProducer<>(props)) {
            p.send(new ProducerRecord<>(JaxRSApp.INSTANCES_TOPIC, key, value));
        }
    }


    private Properties getInstanceProps(String servers, String registry) {
        final Properties props = new Properties();

        final SpecificAvroSerde<AlarmInstance> VALUE_SERDE = new SpecificAvroSerde<>();

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

        String key = name;

        Properties props = getInstanceProps(JaxRSApp.BOOTSTRAP_SERVERS, JaxRSApp.SCHEMA_REGISTRY);

        try(KafkaProducer<String, AlarmClass> p = new KafkaProducer<>(props)) {
            p.send(new ProducerRecord<>(JaxRSApp.CLASSES_TOPIC, key, null));
        }
    }

    @PUT
    @Path("/class")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void putClass(
            @FormParam("name") @NotNull(message = "class name is required") String name,
            @FormParam("priority") @NotNull(message = "priority is required") String priority,
            @FormParam("category") @NotNull(message = "category is required") String category,
            @FormParam("rationale") @NotNull(message = "rationale is required") String rationale,
            @FormParam("correctiveaction") @NotNull(message = "correctiveaction is required") String correctiveaction,
            @FormParam("pocusername") @NotNull(message = "pocusername is required") String pocusername,
            @FormParam("filterable") Boolean filterable,
            @FormParam("latching") Boolean latching,
            @FormParam("ondelayseconds") Long ondelayseconds,
            @FormParam("offdelayseconds") Long offdelayseconds)
    {
        System.out.println("PUT received: " + name);

        String key = name;

        AlarmClass value = new AlarmClass();

        value.setRationale(rationale);

        AlarmPriority ap = null;
        if(priority != null) {
            ap = AlarmPriority.valueOf(priority);
        }
        value.setPriority(ap);

        value.setCategory(category);
        value.setCorrectiveaction(correctiveaction);
        value.setPointofcontactusername(pocusername);
        value.setFilterable(filterable);
        value.setLatching(latching);
        value.setOndelayseconds(ondelayseconds);
        value.setOffdelayseconds(offdelayseconds);

        Properties props = getClassProps(JaxRSApp.BOOTSTRAP_SERVERS, JaxRSApp.SCHEMA_REGISTRY);

        try(KafkaProducer<String, AlarmClass> producer = new KafkaProducer<>(props)) {
            producer.send(new ProducerRecord<>(JaxRSApp.CLASSES_TOPIC, key, value));
        }
    }

    private Properties getClassProps(String servers, String registry) {
        final Properties props = new Properties();

        final SpecificAvroSerde<AlarmClass> VALUE_SERDE = new SpecificAvroSerde<>();

        props.put("bootstrap.servers", servers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", VALUE_SERDE.serializer().getClass().getName());

        // Serializer specific configs
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, registry);

        return props;
    }
}

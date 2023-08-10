package org.jlab.jaws;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.jlab.jaws.clients.ClassProducer;
import org.jlab.jaws.clients.InstanceProducer;
import org.jlab.jaws.entity.*;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Properties;

@Path("/rest")
public class REST {

    private Properties getProducerProps() {
        final Properties props = new Properties();

        props.put("bootstrap.servers", JaxRSApp.BOOTSTRAP_SERVERS);

        return props;
    }

    private Properties getProducerPropsWithRegistry() {
        final Properties props = getProducerProps();

        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, JaxRSApp.SCHEMA_REGISTRY);

        return props;
    }

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
    public void deleteInstance(
        @FormParam("name") @NotNull(message = "alarm name is required") String name) {
        System.err.println("Deleting registration: " + name);

        String key = name;

        Properties props = getProducerPropsWithRegistry();

        try(InstanceProducer p = new InstanceProducer(props)) {
            p.send(key, null);
        }
    }

    @PUT
    @Path("/instance")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void putInstance(@FormParam("name") @NotNull(message = "alarm name is required") String name,
                            @FormParam("class") @NotNull(message = "class is required") String alarmclass,
                            @FormParam("expression") String expression,
                            @FormParam("location") List<String> location,
                            @FormParam("maskedby") String maskedby,
                            @FormParam("screencommand") String screencommand,
                            @FormParam("epicspv") String epicspv)
    {
        System.out.println("PUT received: " + name);

        String key = name;

        AlarmInstance value = new AlarmInstance();

        value.setAlarmclass(alarmclass);

        Object source = new Source();

        if(epicspv != null) {
            source = new EPICSSource(epicspv);
        } else if(expression != null) {
            source = new CALCSource(expression);
        }

        value.setSource(source);
        value.setLocation(location);
        value.setMaskedby(maskedby);
        value.setScreencommand(screencommand);

        Properties props = getProducerPropsWithRegistry();

        try(InstanceProducer p = new InstanceProducer(props)) {
            p.send(key, value);
        }
    }

    @DELETE
    @Path("/class")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void deleteClass(
            @FormParam("name") @NotNull(message = "class name is required") String name) {
        System.err.println("Deleting class: " + name);

        String key = name;

        Properties props = getProducerPropsWithRegistry();

        try(ClassProducer p = new ClassProducer(props)) {
            p.send(key, null);
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
            @FormParam("latchable") Boolean latchable,
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
        value.setLatchable(latchable);
        value.setOndelayseconds(ondelayseconds);
        value.setOffdelayseconds(offdelayseconds);

        Properties props = getProducerPropsWithRegistry();

        try(ClassProducer producer = new ClassProducer(props)) {
            producer.send(key, value);
        }
    }
}

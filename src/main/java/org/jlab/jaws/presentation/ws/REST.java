package org.jlab.jaws.presentation.ws;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.jlab.jaws.business.util.KafkaConfig;
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

    @GET
    @Path("priorities")
    @Produces("application/json")
    public String getPriorities() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(AlarmPriority.values());
    }
}

package org.jlab.jaws.presentation.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.*;
import org.jlab.jaws.entity.*;

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

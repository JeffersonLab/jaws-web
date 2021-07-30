package org.jlab.jaws;

import org.eclipse.microprofile.openapi.annotations.Operation;

import javax.ws.rs.*;

@Path("/rest")
public class REST {
    @PUT
    @Operation(description = "Set registration")
    public void putRegistration()
    {
        System.out.println("PUT received");
    }
}

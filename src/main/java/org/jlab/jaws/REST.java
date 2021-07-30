package org.jlab.jaws;


import javax.ws.rs.*;

@Path("/rest")
public class REST {
    @PUT
    public void putRegistration()
    {
        System.out.println("PUT received");
    }
}

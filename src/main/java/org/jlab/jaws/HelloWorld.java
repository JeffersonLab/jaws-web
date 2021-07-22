package org.jlab.jaws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public class HelloWorld {
    @GET
    public String getMsg()
    {
        return "Hello World !! - Jersey 2";
    }
}

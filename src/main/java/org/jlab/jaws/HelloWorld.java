package org.jlab.jaws;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

@Path("/hello")
public class HelloWorld {
    @GET
    public String getMsg()
    {
        return "Hello World !! - Jersey 2";
    }
}

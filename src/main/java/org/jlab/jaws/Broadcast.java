package org.jlab.jaws;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

@ApplicationScoped
@Path("/broadcast")
public class Broadcast {
    private Sse sse;
    private SseBroadcaster broadcaster;

    @Context
    public void setSse(Sse sse) {
        this.sse = sse;
        this.broadcaster = sse.newBroadcaster();
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context SseEventSink sink) {
        this.broadcaster.register(sink);
        System.err.println("Registered");
    }

    @PUT
    public String broadcastMessage(@QueryParam("message") String message) {
        final OutboundSseEvent event = sse.newEventBuilder()
                .name("message")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, message)
                .build();

        broadcaster.broadcast(event);

        System.err.println("broadcast: " + message);

        return "Message '" + message + "' has been broadcast.";
    }
}

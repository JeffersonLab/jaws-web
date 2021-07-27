package org.jlab.jaws;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

@ApplicationScoped
@Path("/proxy")
public class WebProxy {
    private Sse sse;

    @Context
    public void setSse(Sse sse) {
        this.sse = sse;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context final SseEventSink sink) {
        System.err.println("Proxy connected");

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(!sink.isClosed())

                {
                    System.err.println("Looping");
                    sink.send(sse.newEvent("registration", "hey oh"));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.err.println("Proxy disconnected");
                // Clean up resources here
            }
        });

        thread.start();
    }
}

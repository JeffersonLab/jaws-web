package org.jlab.jaws;

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.jlab.jaws.entity.RegisteredAlarm;
import org.jlab.jaws.eventsource.EventSourceConfig;
import org.jlab.jaws.eventsource.EventSourceListener;
import org.jlab.jaws.eventsource.EventSourceRecord;
import org.jlab.jaws.eventsource.EventSourceTable;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@ApplicationScoped
@Path("/sse")
public class SSE {
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
                final String servers = "localhost:9094";
                final String registry = "http://localhost:8081";

                final Properties props = new Properties();

                final SpecificAvroSerde<RegisteredAlarm> VALUE_SERDE = new SpecificAvroSerde<>();

                props.put(EventSourceConfig.EVENT_SOURCE_GROUP, "sse-proxy-" + Instant.now().toString() + "-" + Math.random());
                props.put(EventSourceConfig.EVENT_SOURCE_TOPIC, "registered-alarms");
                props.put(EventSourceConfig.EVENT_SOURCE_BOOTSTRAP_SERVERS, servers);
                props.put(EventSourceConfig.EVENT_SOURCE_KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
                props.put(EventSourceConfig.EVENT_SOURCE_VALUE_DESERIALIZER, VALUE_SERDE.deserializer().getClass().getName());

                // Deserializer specific configs
                props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, registry);
                props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG,"true");

                EventSourceTable<String, RegisteredAlarm> table = new EventSourceTable<>(props);

                table.addListener(new EventSourceListener<String, RegisteredAlarm>() {
                    @Override
                    public void initialState(Set<EventSourceRecord<String, RegisteredAlarm>> records) {
                        sendRecords(sink, records);
                    }

                    @Override
                    public void changes(List<EventSourceRecord<String, RegisteredAlarm>> records) {
                        sendRecords(sink, records);
                    }

                });

                try {
                    table.start();

                    while (!sink.isClosed()) {
                        System.err.println("Looping checking for disconnect");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    if(table != null) {
                        table.close();
                    }
                }

                System.err.println("Proxy disconnected");
            }
        });

        thread.start();
    }

    private void sendRecords(SseEventSink sink, Collection<EventSourceRecord<String, RegisteredAlarm>> records) {
        for (EventSourceRecord<String, RegisteredAlarm> record : records) {
            String key = record.getKey();
            RegisteredAlarm value = record.getValue();
            String json = "{\"key\": \"" + key + "\"}";
            sink.send(sse.newEvent("registration", json));
        }
    }
}

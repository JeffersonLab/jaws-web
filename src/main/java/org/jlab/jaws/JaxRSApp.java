package org.jlab.jaws;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/proxy")
public class JaxRSApp extends Application {
    public static final String BOOTSTRAP_SERVERS = System.getenv("BOOTSTRAP_SERVERS");
    public static final String SCHEMA_REGISTRY = System.getenv("SCHEMA_REGISTRY");

    public static final String REGISTRATION_TOPIC = "alarm-registrations";
    public static final String CLASSES_TOPIC = "alarm-classes";


    static {
        System.err.println("Using BOOTSTRAP_SERVERS = " + BOOTSTRAP_SERVERS);
        System.err.println("Using SCHEMA_REGISTRY = " + SCHEMA_REGISTRY);
    }
}



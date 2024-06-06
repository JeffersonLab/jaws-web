package org.jlab.jaws.business.session;

import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.LocationProducer;
import org.jlab.jaws.entity.*;
import org.jlab.jaws.persistence.entity.Action;
import org.jlab.jaws.persistence.entity.Alarm;
import org.jlab.jaws.persistence.entity.Component;
import org.jlab.jaws.persistence.entity.Location;

import javax.annotation.PostConstruct;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
@Singleton
@Startup
@RunAs("jaws-admin")
public class KafkaRegistrationFacade {
    private static final Logger LOG = Logger.getLogger(KafkaRegistrationFacade.class.getName());

    @EJB
    LocationFacade locationFacade;
    @EJB
    ComponentFacade componentFacade;
    @EJB
    ActionFacade actionFacade;
    @EJB
    AlarmFacade alarmFacade;
    @EJB
    ServerStatus status;

    @PostConstruct
    private void init() {
        if(System.getenv("SKIP_SEND_REGISTRATION_TO_KAFKA") == null) {
            LOG.log(Level.WARNING, "Sending registrations to Kafka");
            populateLocations();
            populateComponents();
            populateActions();
            populateAlarms();
        } else {
            LOG.log(Level.WARNING, "Skipping send registration to Kafka");
        }
        status.setRegistrationsSent();
    }

    private void populateLocations() {
        List<Location> locationList = locationFacade.findAll(new AbstractFacade.OrderDirective("name"));

        if(locationList != null) {
            try (LocationProducer producer = new LocationProducer(KafkaConfig.getProducerPropsWithRegistry())) {
                for (Location location : locationList) {
                    String key = location.getName();

                    AlarmLocation value = new AlarmLocation();

                    value.setParent(location.getParent() == null ? null : location.getParent().getName());

                    producer.send(key, value);
                }
            }
        }
    }

    private void populateComponents() {
        List<Component> componentList = componentFacade.findAll(new AbstractFacade.OrderDirective("name"));

        componentFacade.kafkaSet(componentList);
    }

    private void populateActions() {
        List<Action> actionList = actionFacade.findAll(new AbstractFacade.OrderDirective("name"));

        actionFacade.kafkaSet(actionList);
    }

    private void populateAlarms() {
        List<Alarm> alarmList = alarmFacade.findAll(new AbstractFacade.OrderDirective("name"));

        alarmFacade.kafkaSet(alarmList);
    }
}

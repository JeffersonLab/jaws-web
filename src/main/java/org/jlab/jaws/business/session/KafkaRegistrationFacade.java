package org.jlab.jaws.business.session;

import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.CategoryProducer;
import org.jlab.jaws.clients.ClassProducer;
import org.jlab.jaws.clients.InstanceProducer;
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

    @PostConstruct
    private void init() {
        populateLocations();
        populateComponents();
        populateActions();
        populateAlarms();
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

        if(componentList != null) {
            try (CategoryProducer producer = new CategoryProducer(KafkaConfig.getProducerPropsWithRegistry())) {
                for (Component component : componentList) {
                    String key = component.getName();

                    String value = "";

                    producer.send(key, value);
                }
            }
        }
    }

    private void populateActions() {
        List<Action> actionList = actionFacade.findAll(new AbstractFacade.OrderDirective("name"));

        if(actionList != null) {
            try(ClassProducer producer = new ClassProducer(KafkaConfig.getProducerPropsWithRegistry())) {
                for (Action action : actionList) {
                    String key = action.getName();

                    AlarmClass value = new AlarmClass();

                    value.setRationale(action.getRationale());

                    String priorityName = action.getPriority().getName();
                    AlarmPriority ap = AlarmPriority.valueOf(priorityName);
                    value.setPriority(ap);

                    value.setCategory(action.getComponent().getName());
                    value.setCorrectiveaction(action.getCorrectiveAction());
                    value.setPointofcontactusername(action.getComponent().getTeam().getName());
                    value.setFilterable(action.isFilterable());
                    value.setLatchable(action.isLatchable());

                    Long onDelaySeconds = null;
                    if(action.getOnDelaySeconds() != null) {
                        onDelaySeconds = action.getOnDelaySeconds().longValue();
                    }
                    value.setOndelayseconds(onDelaySeconds);

                    Long offDelaySeconds = null;
                    if(action.getOffDelaySeconds() != null) {
                        offDelaySeconds = action.getOffDelaySeconds().longValue();
                    }
                    value.setOffdelayseconds(offDelaySeconds);

                    producer.send(key, value);
                }
            }
        }
    }

    private void populateAlarms() {
        List<Alarm> alarmList = alarmFacade.findAll(new AbstractFacade.OrderDirective("name"));

        if(alarmList != null) {
            try (InstanceProducer producer = new InstanceProducer(KafkaConfig.getProducerPropsWithRegistry())) {
                for (Alarm alarm : alarmList) {
                    String key = alarm.getName();

                    AlarmInstance value = new AlarmInstance();

                    value.setAlarmclass(alarm.getAction().getName());

                    Object source = new Source();

                    if(alarm.getPv() != null) {
                        source = new EPICSSource(alarm.getPv());
                    }

                    value.setSource(source);
                    value.setLocation(alarm.getLocationNameList());
                    value.setMaskedby(alarm.getMaskedBy());
                    value.setScreencommand(alarm.getScreenCommand());

                    producer.send(key, value);
                }
            }
        }
    }
}

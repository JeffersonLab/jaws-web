package org.jlab.jaws.business.session;

import org.jlab.jaws.clients.EffectiveNotificationConsumer;
import org.jlab.jaws.entity.EffectiveNotification;
import org.jlab.jaws.persistence.entity.Alarm;
import org.jlab.jaws.presentation.ws.SSE;
import org.jlab.kafka.eventsource.EventSourceListener;
import org.jlab.kafka.eventsource.EventSourceRecord;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
@Singleton
@Startup
@RunAs("jaws-admin")
public class KafkaFacade {
    private static final Logger LOG = Logger.getLogger(KafkaFacade.class.getName());

    private EffectiveNotificationConsumer notificationConsumer = null;

    @EJB
    AlarmFacade alarmFacade;
    @EJB
    NotificationFacade notificationFacade;

    @PostConstruct
    private void init() {
        notificationFacade.clearCache();

        final Properties notificationProps = SSE.getConsumerPropsWithRegistry(-1, false);
        notificationConsumer = new EffectiveNotificationConsumer(notificationProps);
        EventSourceListener<String, EffectiveNotification> notificationListener = new NotificationListener<>();
        notificationConsumer.addListener(notificationListener);
        notificationConsumer.start();
    }

    @PreDestroy
    private void cleanup() {
        if(notificationConsumer != null) {
            notificationConsumer.close();
        }
    }

    @RunAs("jaws-admin")
    class NotificationListener<K, V> implements EventSourceListener<String, EffectiveNotification> {
        @Override
        public void batch(List<EventSourceRecord<String, EffectiveNotification>> records, boolean highWaterReached) {
            for (EventSourceRecord<String, EffectiveNotification> record : records) {
                Alarm alarm = alarmFacade.findByName(record.getKey());

                if(alarm != null) {
                    notificationFacade.set(alarm, record.getValue());
                } else {
                    LOG.warning("Notification of unknown alarm: " + record.getKey());
                }
            }
        }
    }
}

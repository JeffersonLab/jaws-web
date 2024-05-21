package org.jlab.jaws.business.session;

import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.EffectiveNotificationConsumer;
import org.jlab.jaws.clients.OverrideConsumer;
import org.jlab.jaws.entity.AlarmOverrideKey;
import org.jlab.jaws.entity.AlarmOverrideUnion;
import org.jlab.jaws.entity.EffectiveNotification;
import org.jlab.jaws.entity.OverriddenAlarmType;
import org.jlab.jaws.persistence.entity.Alarm;
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
public class KafkaOverrideFacade {
    private static final Logger LOG = Logger.getLogger(KafkaOverrideFacade.class.getName());

    private OverrideConsumer consumer = null;

    @EJB
    AlarmFacade alarmFacade;
    @EJB
    OverrideFacade overrideFacade;

    @PostConstruct
    private void init() {
        final Properties props = KafkaConfig.getConsumerPropsWithRegistry(-1, false);
        consumer = new OverrideConsumer(props);
        EventSourceListener<AlarmOverrideKey, AlarmOverrideUnion> listener = new OverrideListener<>();
        consumer.addListener(listener);
        consumer.start();
    }

    @PreDestroy
    private void cleanup() {
        if(consumer != null) {
            consumer.close();
        }
    }

    class OverrideListener<K, V> implements EventSourceListener<AlarmOverrideKey, AlarmOverrideUnion> {
        @Override
        public void batch(List<EventSourceRecord<AlarmOverrideKey, AlarmOverrideUnion>> records, boolean highWaterReached) {
            for (EventSourceRecord<AlarmOverrideKey, AlarmOverrideUnion> record : records) {
                Alarm alarm = alarmFacade.findByName(record.getKey().getName());
                OverriddenAlarmType type = record.getKey().getType();

                if(alarm != null) {
                    overrideFacade.set(alarm, type, record.getValue());
                } else {
                    LOG.warning("Override of unknown alarm: " + record.getKey());
                }
            }
        }
    }
}

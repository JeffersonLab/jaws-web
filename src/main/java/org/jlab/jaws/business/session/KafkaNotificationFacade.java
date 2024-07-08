package org.jlab.jaws.business.session;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.jlab.jaws.business.service.BatchNotificationService;
import org.jlab.jaws.business.util.KafkaConfig;
import org.jlab.jaws.clients.EffectiveNotificationConsumer;
import org.jlab.jaws.entity.EffectiveNotification;
import org.jlab.kafka.eventsource.EventSourceListener;
import org.jlab.kafka.eventsource.EventSourceRecord;

/**
 * @author ryans
 */
@Singleton
@Startup
@RunAs("jaws-admin")
public class KafkaNotificationFacade {
  private static final Logger LOG = Logger.getLogger(KafkaNotificationFacade.class.getName());

  private EffectiveNotificationConsumer notificationConsumer = null;

  @EJB AlarmFacade alarmFacade;
  @EJB NotificationFacade notificationFacade;

  @PostConstruct
  private void init() {
    if (System.getenv("SKIP_NOTIFICATION_MERGE") == null) {
      LOG.log(Level.WARNING, "Monitoring Kafka for notifications");
      notificationFacade.clearCache();

      final Properties notificationProps = KafkaConfig.getConsumerPropsWithRegistry(-1, false);
      notificationConsumer = new EffectiveNotificationConsumer(notificationProps);
      EventSourceListener<String, EffectiveNotification> notificationListener =
          new NotificationListener<>();
      notificationConsumer.addListener(notificationListener);
      notificationConsumer.start();
    } else {
      LOG.log(Level.WARNING, "Skipping monitoring Kafka for notifications");
    }
  }

  @PreDestroy
  private void cleanup() {
    if (notificationConsumer != null) {
      notificationConsumer.close();
    }
  }

  class NotificationListener<K, V> implements EventSourceListener<String, EffectiveNotification> {
    @Override
    public void batch(
        List<EventSourceRecord<String, EffectiveNotification>> records, boolean highWaterReached) {
      try {
        long start = System.currentTimeMillis();
        notificationFacade.oracleMerge(records);
        long end = System.currentTimeMillis();

        // TODO: Consider moving history updates to a separate thread to avoid blocking notification
        // merge
        long hStart = System.currentTimeMillis();
        BatchNotificationService service = new BatchNotificationService();
        service.oracleMergeHistory(records);
        long hEnd = System.currentTimeMillis();

        LOG.log(
            Level.INFO,
            "Merged {0} batch notifications in {1} milliseconds, merged history in {2} milliseconds",
            new Object[] {records.size(), end - start, hEnd - hStart});

      } catch (SQLException e) {
        LOG.log(Level.SEVERE, "Unable to merge Kafka notifications into Oracle", e);
      }
    }
  }
}

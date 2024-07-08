package org.jlab.jaws.integration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import org.jlab.jaws.business.service.BatchNotificationService;
import org.jlab.jaws.entity.AlarmState;
import org.jlab.jaws.entity.EffectiveNotification;
import org.jlab.kafka.eventsource.EventSourceRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MergeNotificationHistoryTest {

  @Before
  public void setup() throws SQLException, NamingException {
    new TestJndiContextFactory();
    new TestOracleDataSource();
  }

  @After
  public void teardown() {}

  @Test
  public void doTest() throws SQLException {
    BatchNotificationService service = new BatchNotificationService();

    List<EventSourceRecord<String, EffectiveNotification>> records = new ArrayList<>();

    long offset = 0;
    long timestamp =
        (new Date(0))
            .getTime(); // We use fixed start date so we can test replay of same duplicate messages
    // by running this test case multiple times

    records.add(
        new EventSourceRecord<>(
            "Alarm1",
            new EffectiveNotification(null, null, AlarmState.Active),
            offset++,
            timestamp));
    timestamp = timestamp + 1000;
    records.add(
        new EventSourceRecord<>(
            "Alarm1",
            new EffectiveNotification(null, null, AlarmState.Normal),
            offset++,
            timestamp));
    timestamp = timestamp + 1000;
    records.add(
        new EventSourceRecord<>(
            "Alarm1",
            new EffectiveNotification(null, null, AlarmState.Active),
            offset++,
            timestamp));
    timestamp = timestamp + 1000;
    records.add(
        new EventSourceRecord<>(
            "Alarm1",
            new EffectiveNotification(null, null, AlarmState.Normal),
            offset++,
            timestamp));
    timestamp = timestamp + 1000;
    records.add(
        new EventSourceRecord<>(
            "Alarm1",
            new EffectiveNotification(null, null, AlarmState.Active),
            offset++,
            timestamp));
    timestamp = timestamp + 1000;
    records.add(
        new EventSourceRecord<>(
            "Alarm1",
            new EffectiveNotification(null, null, AlarmState.Normal),
            offset++,
            timestamp));
    timestamp = timestamp + 1000;
    records.add(
        new EventSourceRecord<>(
            "Alarm1",
            new EffectiveNotification(null, null, AlarmState.Normal),
            offset++,
            timestamp));
    timestamp = timestamp + 1000;
    records.add(
        new EventSourceRecord<>(
            "Alarm1",
            new EffectiveNotification(null, null, AlarmState.Active),
            offset++,
            timestamp));
    timestamp = timestamp + 1000;
    records.add(
        new EventSourceRecord<>(
            "Alarm1",
            new EffectiveNotification(null, null, AlarmState.Active),
            offset++,
            timestamp));

    service.oracleMergeHistory(records);
  }
}

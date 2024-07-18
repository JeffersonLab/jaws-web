package org.jlab.jaws.business.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import org.jlab.jaws.business.session.NotificationFacade;
import org.jlab.jaws.business.util.OracleUtil;
import org.jlab.jaws.entity.*;
import org.jlab.jaws.persistence.model.BinaryState;
import org.jlab.jaws.persistence.model.SuppressedState;
import org.jlab.kafka.eventsource.EventSourceRecord;

public class BatchNotificationService {
  public void oracleMergeActiveHistory(
      List<EventSourceRecord<String, EffectiveNotification>> records) throws SQLException {
    String sql = "{call JAWS_OWNER.MERGE_ACTIVE_HISTORY(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    Connection con = null;
    PreparedStatement stmt = null;

    try {
      con = OracleUtil.getConnection();

      // Use default autoCommit and Transaction Isolation Level (explicitly stated)
      con.setAutoCommit(true);
      con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      stmt = con.prepareCall(sql);

      for (EventSourceRecord<String, EffectiveNotification> record : records) {
        AlarmActivationUnion union = record.getValue().getActivation();

        BinaryState state = BinaryState.fromAlarmState(record.getValue().getState());

        String activationType = "NotActive";
        String note = null;
        String sevr = null;
        String stat = null;
        String error = null;
        String incitedWith = getIncitedWith(record.getValue().getState());

        if (union != null) {
          if (union.getUnion() instanceof EPICSActivation) {
            activationType = "EPICS";
            EPICSActivation epics = (EPICSActivation) union.getUnion();
            sevr = epics.getSevr().name();
            stat = epics.getStat().name();
          } else if (union.getUnion() instanceof NoteActivation) {
            activationType = "Note";
            NoteActivation noteObj = (NoteActivation) union.getUnion();
            note = noteObj.getNote();
          } else if (union.getUnion() instanceof ChannelErrorActivation) {
            activationType = "ChannelError";
            ChannelErrorActivation channel = (ChannelErrorActivation) union.getUnion();
            error = channel.getError();
          } else if (union.getUnion() instanceof Activation) {
            activationType = "Simple";
          }
        }

        stmt.setString(1, record.getKey());
        stmt.setDate(2, new java.sql.Date(record.getTimestamp()));
        stmt.setString(3, BinaryState.Normal == state ? "Y" : "N");
        stmt.setString(4, activationType);
        if (note == null) {
          stmt.setNull(5, Types.VARCHAR);
        } else {
          stmt.setString(5, note);
        }
        if (sevr == null) {
          stmt.setNull(6, Types.VARCHAR);
        } else {
          stmt.setString(6, sevr);
        }
        if (stat == null) {
          stmt.setNull(7, Types.VARCHAR);
        } else {
          stmt.setString(7, stat);
        }
        if (error == null) {
          stmt.setNull(8, Types.VARCHAR);
        } else {
          stmt.setString(8, error);
        }
        if (incitedWith == null) {
          stmt.setNull(9, Types.VARCHAR);
        } else {
          stmt.setString(9, incitedWith);
        }

        stmt.addBatch();
      }

      stmt.executeBatch();
    } finally {
      OracleUtil.close(stmt, con);
    }
  }

  private String getIncitedWith(AlarmState state) {
    String incitedWith = null;

    switch (state) {
      case ActiveOffDelayed:
        incitedWith = "OffDelayed";
        break;
      case ActiveLatched:
        incitedWith = "Latched";
        break;
    }

    return incitedWith;
  }

  private String getSuppressedWith(AlarmState state) {
    String suppressedWith = null;

    switch (state) {
      case NormalDisabled:
        suppressedWith = "Disabled";
        break;
      case NormalFiltered:
        suppressedWith = "Filtered";
        break;
      case NormalMasked:
        suppressedWith = "Masked";
        break;
      case NormalContinuousShelved:
      case NormalOneShotShelved:
        suppressedWith = "Shelved";
        break;
      case NormalOnDelayed:
        suppressedWith = "OnDelayed";
        break;
    }

    return suppressedWith;
  }

  public void oracleMergeSuppressedHistory(
      List<EventSourceRecord<String, EffectiveNotification>> records) throws SQLException {
    String sql =
        "{call JAWS_OWNER.MERGE_SUPPRESSED_HISTORY(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    Connection con = null;
    PreparedStatement stmt = null;

    try {
      con = OracleUtil.getConnection();

      // Use default autoCommit and Transaction Isolation Level (explicitly stated)
      con.setAutoCommit(true);
      con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      stmt = con.prepareCall(sql);

      for (EventSourceRecord<String, EffectiveNotification> record : records) {
        AlarmActivationUnion union = record.getValue().getActivation();

        SuppressedState state = SuppressedState.fromAlarmState(record.getValue().getState());

        String activationType = "NotActive";
        String note = null;
        String sevr = null;
        String stat = null;
        String error = null;
        String suppressedWith = getSuppressedWith(record.getValue().getState());
        String comments = null;
        boolean oneshot = AlarmState.NormalOneShotShelved.equals(record.getValue().getState());
        Date expiration = null;
        String reason = null;

        if (union != null) {
          if (union.getUnion() instanceof EPICSActivation) {
            activationType = "EPICS";
            EPICSActivation epics = (EPICSActivation) union.getUnion();
            sevr = epics.getSevr().name();
            stat = epics.getStat().name();
          } else if (union.getUnion() instanceof NoteActivation) {
            activationType = "Note";
            NoteActivation noteObj = (NoteActivation) union.getUnion();
            note = noteObj.getNote();
          } else if (union.getUnion() instanceof ChannelErrorActivation) {
            activationType = "ChannelError";
            ChannelErrorActivation channel = (ChannelErrorActivation) union.getUnion();
            error = channel.getError();
          } else if (union.getUnion() instanceof Activation) {
            activationType = "Simple";
          }
        }

        stmt.setString(1, record.getKey());
        stmt.setDate(2, new java.sql.Date(record.getTimestamp()));
        stmt.setString(3, SuppressedState.NOT_SUPPRESSED == state ? "Y" : "N");
        stmt.setString(4, activationType);
        if (note == null) {
          stmt.setNull(5, Types.VARCHAR);
        } else {
          stmt.setString(5, note);
        }
        if (sevr == null) {
          stmt.setNull(6, Types.VARCHAR);
        } else {
          stmt.setString(6, sevr);
        }
        if (stat == null) {
          stmt.setNull(7, Types.VARCHAR);
        } else {
          stmt.setString(7, stat);
        }
        if (error == null) {
          stmt.setNull(8, Types.VARCHAR);
        } else {
          stmt.setString(8, error);
        }
        if (suppressedWith == null) {
          stmt.setNull(9, Types.VARCHAR);
        } else {
          stmt.setString(9, suppressedWith);
        }
        if (comments == null) {
          stmt.setNull(10, Types.VARCHAR);
        } else {
          stmt.setString(10, comments);
        }
        stmt.setString(11, oneshot ? "Y" : "N");
        if (expiration == null) {
          stmt.setNull(12, Types.VARCHAR);
        } else {
          stmt.setDate(12, new java.sql.Date(expiration.getTime()));
        }
        if (reason == null) {
          stmt.setNull(13, Types.VARCHAR);
        } else {
          stmt.setString(13, reason);
        }

        stmt.addBatch();
      }

      stmt.executeBatch();
    } finally {
      OracleUtil.close(stmt, con);
    }
  }

  public void oracleInsertNotificationHistory(
      List<EventSourceRecord<String, EffectiveNotification>> records) throws SQLException {
    String sql =
        "insert into jaws_owner.notification_history(notification_history_id, offset, name, state, since, "
            + "active_override, activation_type, activation_note, activation_sevr, activation_stat, activation_error) "
            + "values(jaws_owner.notification_history_id.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    Connection con = null;
    PreparedStatement stmt = null;

    try {
      con = OracleUtil.getConnection();

      // Use default autoCommit and Transaction Isolation Level (explicitly stated)
      con.setAutoCommit(true);
      con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

      stmt = con.prepareCall(sql);

      for (EventSourceRecord<String, EffectiveNotification> record : records) {
        AlarmActivationUnion union = record.getValue().getActivation();

        String state = BinaryState.fromAlarmState(record.getValue().getState()).name();
        OverriddenAlarmType override =
            NotificationFacade.overrideFromAlarmState(record.getValue().getState());
        String activationType = "NotActive";
        String note = null;
        String sevr = null;
        String stat = null;
        String error = null;

        if (union != null) {
          if (union.getUnion() instanceof EPICSActivation) {
            activationType = "EPICS";
            EPICSActivation epics = (EPICSActivation) union.getUnion();
            sevr = epics.getSevr().name();
            stat = epics.getStat().name();
          } else if (union.getUnion() instanceof NoteActivation) {
            activationType = "Note";
            NoteActivation noteObj = (NoteActivation) union.getUnion();
            note = noteObj.getNote();
          } else if (union.getUnion() instanceof ChannelErrorActivation) {
            activationType = "ChannelError";
            ChannelErrorActivation channel = (ChannelErrorActivation) union.getUnion();
            error = channel.getError();
          } else if (union.getUnion() instanceof Activation) {
            activationType = "Simple";
          }
        }

        stmt.setLong(1, record.getOffset());
        stmt.setString(2, record.getKey());
        stmt.setString(3, state);
        stmt.setDate(4, new java.sql.Date(record.getTimestamp()));
        if (override == null) {
          stmt.setNull(5, Types.VARCHAR);
        } else {
          stmt.setString(5, override.name());
        }
        stmt.setString(6, activationType);
        if (note == null) {
          stmt.setNull(7, Types.VARCHAR);
        } else {
          stmt.setString(7, note);
        }
        if (sevr == null) {
          stmt.setNull(8, Types.VARCHAR);
        } else {
          stmt.setString(8, sevr);
        }
        if (stat == null) {
          stmt.setNull(9, Types.VARCHAR);
        } else {
          stmt.setString(9, stat);
        }
        if (error == null) {
          stmt.setNull(10, Types.VARCHAR);
        } else {
          stmt.setString(10, error);
        }

        stmt.addBatch();
      }

      stmt.executeBatch();
    } finally {
      OracleUtil.close(stmt, con);
    }
  }
}

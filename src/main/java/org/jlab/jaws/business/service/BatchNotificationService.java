package org.jlab.jaws.business.service;

import org.jlab.jaws.business.util.OracleUtil;
import org.jlab.jaws.entity.*;
import org.jlab.jaws.persistence.model.BinaryState;
import org.jlab.kafka.eventsource.EventSourceRecord;

import javax.annotation.security.PermitAll;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class BatchNotificationService {
    private void oracleMergeHistoryUnique(List<EventSourceRecord<String, EffectiveNotification>> records) throws SQLException {
        String sql = "{call JAWS_OWNER.MERGE_NOTIFICATION_HISTORY(?, ?, ?, ?, ?, ?, ?, ?)}";
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = OracleUtil.getConnection();
            stmt = con.prepareCall(sql);

            for(EventSourceRecord<String, EffectiveNotification> record : records) {
                AlarmActivationUnion union = record.getValue().getActivation();

                BinaryState state = BinaryState.fromAlarmState(record.getValue().getState());

                String activationType = "NotActive";
                String note = null;
                String sevr = null;
                String stat = null;
                String error = null;

                if(union != null) {
                    if(union.getUnion() instanceof EPICSActivation) {
                        activationType = "EPICS";
                        EPICSActivation epics = (EPICSActivation) union.getUnion();
                        sevr = epics.getSevr().name();
                        stat = epics.getStat().name();
                    } else if (union.getUnion() instanceof NoteActivation) {
                        activationType = "Note";
                        NoteActivation noteObj = (NoteActivation) union.getUnion();
                        note = noteObj.getNote();
                    } else if(union.getUnion() instanceof ChannelErrorActivation) {
                        activationType = "ChannelError";
                        ChannelErrorActivation channel = (ChannelErrorActivation) union.getUnion();
                        error = channel.getError();
                    } else if(union.getUnion() instanceof Activation) {
                        activationType = "Simple";
                    }
                }

                stmt.setString(1, record.getKey());
                stmt.setDate(2, new java.sql.Date(record.getTimestamp()));
                stmt.setString(3, BinaryState.Normal == state ? "Y" : "N");
                stmt.setString(4, activationType);
                if(note == null) {
                    stmt.setNull(5, Types.VARCHAR);
                } else {
                    stmt.setString(5, note);
                }
                if(sevr == null) {
                    stmt.setNull(6, Types.VARCHAR);
                } else {
                    stmt.setString(6, sevr);
                }
                if(stat == null) {
                    stmt.setNull(7, Types.VARCHAR);
                } else {
                    stmt.setString(7, stat);
                }
                if(error == null) {
                    stmt.setNull(8, Types.VARCHAR);
                } else {
                    stmt.setString(8, error);
                }

                stmt.addBatch();
            }

            stmt.executeBatch();
        } finally {
            OracleUtil.close(stmt, con);
        }
    }

    public void oracleMergeHistory(List<EventSourceRecord<String, EffectiveNotification>> records) throws SQLException {
        List<EventSourceRecord<String, EffectiveNotification>> clone = new ArrayList<>(records);

        // We must maintain insert and update order, event by event, so must parse batches to contain unique alarms

        while(!clone.isEmpty()) {
            Map<String, EventSourceRecord<String, EffectiveNotification>> batch = new LinkedHashMap<>();
            ListIterator<EventSourceRecord<String, EffectiveNotification>> iterator = clone.listIterator();

            while(iterator.hasNext()) {
                EventSourceRecord<String, EffectiveNotification> record = iterator.next();
                if(batch.get(record.getKey()) == null) {
                    batch.put(record.getKey(), record);
                    iterator.remove();
                }
            }

            oracleMergeHistoryUnique(new ArrayList<>(batch.values()));
        }
    }
}

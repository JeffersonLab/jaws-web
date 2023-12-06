package org.jlab.jaws.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "ALARM_SOURCE_EPICS", schema = "JAWS_OWNER")
public class AlarmSourceEpics implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ALARM_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger alarmId;
    @Size(max = 64)
    @Column(length = 64)
    private String pv;

    public BigInteger getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(BigInteger alarmId) {
        this.alarmId = alarmId;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlarmSourceEpics)) return false;
        AlarmSourceEpics that = (AlarmSourceEpics) o;
        return Objects.equals(alarmId, that.alarmId) && Objects.equals(pv, that.pv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarmId, pv);
    }
}

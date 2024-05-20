package org.jlab.jaws.persistence.entity;

import org.jlab.jaws.entity.AlarmState;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "NOTIFICATION", schema = "JAWS_OWNER")
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @JoinColumn(name = "ALARM_ID", referencedColumnName = "ALARM_ID", nullable = false)
    @ManyToOne(optional = false)
    private Alarm alarm;
    @Basic(optional = false)
    @Column(name = "STATE", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AlarmState state;
    @Size(max = 64)
    @NotNull
    @Column(name = "ACTIVATION_TYPE", length = 64, nullable = false)
    private String activationType;
    @Size(max = 128)
    @Column(name = "ACTIVATION_NOTE", length = 128, nullable = true)
    private String activationNote;
    @Size(max = 32)
    @Column(name = "ACTIVATION_SEVR", length = 32, nullable = true)
    private String activationSevr;
    @Size(max = 32)
    @Column(name = "ACTIVATION_STAT", length = 32, nullable = true)
    private String activationStat;
    @Size(max = 128)
    @Column(name = "ACTIVATION_ERROR", length = 128, nullable = true)
    private String activationError;

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }


    public AlarmState getState() {
        return state;
    }

    public void setState(AlarmState state) {
        this.state = state;
    }

    public String getActivationType() {
        return activationType;
    }

    public void setActivationType(String activationType) {
        this.activationType = activationType;
    }

    public String getActivationNote() {
        return activationNote;
    }

    public void setActivationNote(String activationNote) {
        this.activationNote = activationNote;
    }

    public String getActivationSevr() {
        return activationSevr;
    }

    public void setActivationSevr(String activationSevr) {
        this.activationSevr = activationSevr;
    }

    public String getActivationStat() {
        return activationStat;
    }

    public void setActivationStat(String activationStat) {
        this.activationStat = activationStat;
    }

    public String getActivationError() {
        return activationError;
    }

    public void setActivationError(String activationError) {
        this.activationError = activationError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification entity = (Notification) o;
        return Objects.equals(alarm, entity.alarm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarm);
    }
}

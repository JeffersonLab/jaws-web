package org.jlab.jaws.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "NOTIFICATION_HISTORY", schema = "JAWS_OWNER")
public class NotificationHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected NotificationHistoryPK notificationHistoryPK;
    @JoinColumn(name = "NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
    @ManyToOne // This should be OneToOne, but doesn't work given name is an alternate key in Alarm
    private Alarm alarm;
    @Column(name = "ACTIVE_END", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date activeEnd;
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

    public NotificationHistoryPK getNotificationHistoryPK() {
        return notificationHistoryPK;
    }

    public void setNotificationHistoryPK(NotificationHistoryPK notificationHistoryPK) {
        this.notificationHistoryPK = notificationHistoryPK;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public Date getActiveEnd() {
        return activeEnd;
    }

    public void setActiveEnd(Date activeEnd) {
        this.activeEnd = activeEnd;
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
        if (!(o instanceof NotificationHistory)) return false;
        NotificationHistory entity = (NotificationHistory) o;
        return Objects.equals(notificationHistoryPK, entity.notificationHistoryPK);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationHistoryPK);
    }
}

package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jlab.jaws.persistence.model.BinaryState;

@Entity
@Table(name = "NOTIFICATION_HISTORY", schema = "JAWS_OWNER")
public class NotificationHistory implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(
      name = "NotificationHistoryId",
      sequenceName = "NOTIFICATION_HISTORY_ID",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NotificationHistoryId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "NOTIFICATION_HISTORY_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger notificationHistoryId;

  @Column(name = "OFFSET", nullable = false)
  @NotNull
  private BigInteger offset;

  @JoinColumn(name = "NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
  @ManyToOne // This should be OneToOne, but doesn't work given name is an alternate key in Alarm
  private Alarm alarm;

  @Column(name = "SINCE", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  private Date since;

  @Basic(optional = false)
  @Column(name = "STATE", nullable = false, length = 32)
  @Enumerated(EnumType.STRING)
  @NotNull
  private BinaryState state;

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

  @Size(max = 32)
  @Column(name = "ACTIVE_OVERRIDE", length = 32, nullable = true)
  private String activeOverride;

  public @NotNull BigInteger getNotificationHistoryId() {
    return notificationHistoryId;
  }

  public void setNotificationHistoryId(@NotNull BigInteger notificationHistoryId) {
    this.notificationHistoryId = notificationHistoryId;
  }

  public @NotNull BigInteger getOffset() {
    return offset;
  }

  public void setOffset(@NotNull BigInteger offset) {
    this.offset = offset;
  }

  public @NotNull BinaryState getState() {
    return state;
  }

  public void setState(@NotNull BinaryState state) {
    this.state = state;
  }

  public @Size(max = 32) String getActiveOverride() {
    return activeOverride;
  }

  public void setActiveOverride(@Size(max = 32) String activeOverride) {
    this.activeOverride = activeOverride;
  }

  public Alarm getAlarm() {
    return alarm;
  }

  public void setAlarm(Alarm alarm) {
    this.alarm = alarm;
  }

  public Date getSince() {
    return since;
  }

  public void setSince(Date activeEnd) {
    this.since = since;
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

  public @Size(max = 32) String getIncitedWith() {
    return activeOverride;
  }

  public void setIncitedWith(@Size(max = 32) String incitedWith) {
    this.activeOverride = incitedWith;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof NotificationHistory)) return false;
    NotificationHistory entity = (NotificationHistory) o;
    return Objects.equals(notificationHistoryId, entity.notificationHistoryId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(notificationHistoryId);
  }
}

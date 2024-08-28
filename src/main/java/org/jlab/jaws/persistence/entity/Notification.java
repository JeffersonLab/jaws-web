package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jlab.jaws.entity.OverriddenAlarmType;
import org.jlab.jaws.persistence.model.BinaryState;

@Entity
@Table(name = "NOTIFICATION", schema = "JAWS_OWNER")
public class Notification implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @NotNull
  @Size(max = 64)
  @Column(length = 64)
  private String name;

  @JoinColumn(name = "NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
  @ManyToOne // This should be OneToOne, but doesn't work given name is an alternate key in Alarm
  private AlarmEntity alarm;

  @Basic(optional = false)
  @Column(name = "STATE", nullable = false, length = 32)
  @Enumerated(EnumType.STRING)
  @NotNull
  private BinaryState state;

  @Column(name = "SINCE", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  private Date since;

  @Basic(optional = true)
  @Column(name = "ACTIVE_OVERRIDE", nullable = true, length = 32)
  @Enumerated(EnumType.STRING)
  private OverriddenAlarmType override;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AlarmEntity getAlarm() {
    return alarm;
  }

  public void setAlarm(AlarmEntity alarm) {
    this.alarm = alarm;
  }

  public BinaryState getState() {
    return state;
  }

  public void setState(BinaryState state) {
    this.state = state;
  }

  public Date getSince() {
    return since;
  }

  public void setSince(Date since) {
    this.since = since;
  }

  public OverriddenAlarmType getActiveOverride() {
    return override;
  }

  public void setActiveOverride(OverriddenAlarmType override) {
    this.override = override;
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
    return Objects.equals(name, entity.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}

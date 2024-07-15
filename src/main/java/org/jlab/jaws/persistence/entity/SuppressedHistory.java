package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "SUPPRESSED_HISTORY", schema = "JAWS_OWNER")
public class SuppressedHistory implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId protected SuppressedHistoryPK suppressedHistoryPK;

  @JoinColumn(name = "NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
  @ManyToOne // This should be OneToOne, but doesn't work given name is an alternate key in Alarm
  private Alarm alarm;

  @Column(name = "SUPPRESSED_END", nullable = true)
  @Temporal(TemporalType.TIMESTAMP)
  private Date suppressedEnd;

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
  @Column(name = "SUPPRESSED_WITH", length = 32, nullable = true)
  private String suppressedWith;

  public SuppressedHistoryPK getSuppressedHistoryPK() {
    return suppressedHistoryPK;
  }

  public void setSuppressedHistoryPK(SuppressedHistoryPK suppressedHistoryPK) {
    this.suppressedHistoryPK = suppressedHistoryPK;
  }

  public Alarm getAlarm() {
    return alarm;
  }

  public void setAlarm(Alarm alarm) {
    this.alarm = alarm;
  }

  public Date getSuppressedEnd() {
    return suppressedEnd;
  }

  public void setSuppressedEnd(Date suppressedEnd) {
    this.suppressedEnd = suppressedEnd;
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

  public @Size(max = 32) String getSuppressedWith() {
    return suppressedWith;
  }

  public void setIncitedWith(@Size(max = 32) String suppressedWith) {
    this.suppressedWith = suppressedWith;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SuppressedHistory)) return false;
    SuppressedHistory entity = (SuppressedHistory) o;
    return Objects.equals(suppressedHistoryPK, entity.suppressedHistoryPK);
  }

  @Override
  public int hashCode() {
    return Objects.hash(suppressedHistoryPK);
  }
}

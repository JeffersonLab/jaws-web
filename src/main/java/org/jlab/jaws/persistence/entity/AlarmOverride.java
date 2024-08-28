package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.Size;
import org.jlab.smoothness.persistence.util.YnStringToBoolean;

@Entity
@Table(name = "OVERRIDE", schema = "JAWS_OWNER")
public class AlarmOverride implements Serializable, Comparable<AlarmOverride> {
  private static final long serialVersionUID = 1L;

  @EmbeddedId protected OverridePK overridePK;

  @Size(max = 512)
  @Column(name = "COMMENTS", length = 512, nullable = true)
  private String comments;

  @Column(name = "ONESHOT", nullable = false, length = 1)
  @Convert(converter = YnStringToBoolean.class)
  private boolean oneshot;

  @Column(name = "EXPIRATION", nullable = true)
  @Temporal(TemporalType.TIMESTAMP)
  private Date expiration;

  @Column(name = "SHELVED_REASON", nullable = true)
  private String shelvedReason;

  @JoinColumn(name = "NAME", referencedColumnName = "NAME", updatable = false, insertable = false)
  @ManyToOne // This should be OneToOne, but doesn't work given name is an alternate key in Alarm
  private AlarmEntity alarm;

  public OverridePK getOverridePK() {
    return overridePK;
  }

  public void setOverridePK(OverridePK overridePK) {
    this.overridePK = overridePK;
  }

  public AlarmEntity getAlarm() {
    return alarm;
  }

  public void setAlarm(AlarmEntity alarm) {
    this.alarm = alarm;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public boolean isOneshot() {
    return oneshot;
  }

  public void setOneshot(boolean oneshot) {
    this.oneshot = oneshot;
  }

  public Date getExpiration() {
    return expiration;
  }

  public void setExpiration(Date expiration) {
    this.expiration = expiration;
  }

  public String getShelvedReason() {
    return shelvedReason;
  }

  public void setShelvedReason(String shelvedReason) {
    this.shelvedReason = shelvedReason;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AlarmOverride)) return false;
    AlarmOverride that = (AlarmOverride) o;
    return Objects.equals(overridePK, that.overridePK);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(overridePK);
  }

  @Override
  public int compareTo(AlarmOverride o) {
    return this.overridePK.compareTo(o.overridePK);
  }
}

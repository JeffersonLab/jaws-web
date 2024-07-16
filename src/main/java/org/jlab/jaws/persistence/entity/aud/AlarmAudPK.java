package org.jlab.jaws.persistence.entity.aud;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * @author ryans
 */
@Embeddable
public class AlarmAudPK implements Serializable {
  @Basic(optional = false)
  @NotNull
  @Column(name = "ALARM_ID", nullable = false)
  private BigInteger alarmId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "REV", nullable = false)
  private BigInteger rev;

  public AlarmAudPK() {}

  public BigInteger getAlarmId() {
    return alarmId;
  }

  public void setAlarmId(BigInteger alarmId) {
    this.alarmId = alarmId;
  }

  public BigInteger getRev() {
    return rev;
  }

  public void setRev(BigInteger rev) {
    this.rev = rev;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 23 * hash + (this.alarmId != null ? this.alarmId.hashCode() : 0);
    hash = 23 * hash + (this.rev != null ? this.rev.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AlarmAudPK other = (AlarmAudPK) obj;
    if (!Objects.equals(this.alarmId, other.alarmId)) {
      return false;
    }
    return Objects.equals(this.rev, other.rev);
  }
}

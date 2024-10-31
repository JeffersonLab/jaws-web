package org.jlab.jaws.persistence.entity.aud;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author ryans
 */
@Embeddable
public class SyncRuleAudPK implements Serializable {
  @Basic(optional = false)
  @NotNull
  @Column(name = "SYNC_RULE_ID", nullable = false)
  private BigInteger syncRuleId;

  @Basic(optional = false)
  @NotNull
  @Column(name = "REV", nullable = false)
  private BigInteger rev;

  public SyncRuleAudPK() {}

  public BigInteger getSyncRuleId() {
    return syncRuleId;
  }

  public void setSyncRuleId(BigInteger alarmId) {
    this.syncRuleId = alarmId;
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
    hash = 23 * hash + (this.syncRuleId != null ? this.syncRuleId.hashCode() : 0);
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
    final SyncRuleAudPK other = (SyncRuleAudPK) obj;
    if (!Objects.equals(this.syncRuleId, other.syncRuleId)) {
      return false;
    }
    return Objects.equals(this.rev, other.rev);
  }
}

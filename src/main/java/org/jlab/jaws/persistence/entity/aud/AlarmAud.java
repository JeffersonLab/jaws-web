package org.jlab.jaws.persistence.entity.aud;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.envers.RevisionType;
import org.jlab.jaws.persistence.entity.*;

/**
 * @author ryans
 */
@Entity
@Table(name = "ALARM_AUD", schema = "JAWS_OWNER")
public class AlarmAud implements Serializable {

  private static final long serialVersionUID = 1L;
  @EmbeddedId protected AlarmAudPK alarmAudPK;

  @Enumerated(EnumType.ORDINAL)
  @NotNull
  @Column(name = "REVTYPE")
  private RevisionType type;

  @JoinColumn(
      name = "REV",
      referencedColumnName = "REV",
      insertable = false,
      updatable = false,
      nullable = false)
  @NotNull
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private ApplicationRevisionInfo revision;

  public RevisionType getType() {
    return type;
  }

  public void setType(RevisionType type) {
    this.type = type;
  }

  public ApplicationRevisionInfo getRevision() {
    return revision;
  }

  public void setRevision(ApplicationRevisionInfo revision) {
    this.revision = revision;
  }

  public AlarmAudPK getAlarmAudPK() {
    return alarmAudPK;
  }

  public void setAlarmAudPK(AlarmAudPK alarmAudPK) {
    this.alarmAudPK = alarmAudPK;
  }

  @Size(max = 64)
  @Column(length = 64)
  private String name;

  @Size(max = 64)
  @Column(length = 64)
  private String alias;

  @Column(name = "ACTION_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger actionId;

  @Size(max = 64)
  @Column(name = "DEVICE", length = 64, nullable = true)
  private String device;

  @Size(max = 512)
  @Column(name = "SCREEN_COMMAND", length = 512, nullable = true)
  private String screenCommand;

  @Size(max = 64)
  @Column(name = "MASKED_BY", length = 64, nullable = true)
  private String maskedBy;

  @Size(max = 64)
  @Column(length = 64, nullable = true)
  private String pv;

  @Size(max = 64)
  @Column(name = "SYNC_ELEMENT_NAME", length = 64, nullable = true)
  private String syncElementName;

  @Column(name = "SYNC_ELEMENT_ID", nullable = true, precision = 22, scale = 0)
  private BigInteger syncElementId;

  @Column(name = "SYNC_RULE_ID", nullable = true, precision = 22, scale = 0)
  private BigInteger syncRuleId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public BigInteger getActionId() {
    return actionId;
  }

  public void setActionId(BigInteger actionId) {
    this.actionId = actionId;
  }

  public String getDevice() {
    return device;
  }

  public void setDevice(String device) {
    this.device = device;
  }

  public String getScreenCommand() {
    return screenCommand;
  }

  public void setScreenCommand(String screenCommand) {
    this.screenCommand = screenCommand;
  }

  public String getMaskedBy() {
    return maskedBy;
  }

  public void setMaskedBy(String maskedBy) {
    this.maskedBy = maskedBy;
  }

  public String getPv() {
    return pv;
  }

  public void setPv(String pv) {
    this.pv = pv;
  }

  public String getSyncElementName() {
    return syncElementName;
  }

  public void setSyncElementName(String syncElementName) {
    this.syncElementName = syncElementName;
  }

  public BigInteger getSyncElementId() {
    return syncElementId;
  }

  public void setSyncElementId(BigInteger syncElementId) {
    this.syncElementId = syncElementId;
  }

  public BigInteger getSyncRuleId() {
    return syncRuleId;
  }

  public void setSyncRuleId(BigInteger syncRuleId) {
    this.syncRuleId = syncRuleId;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 17 * hash + (this.alarmAudPK != null ? this.alarmAudPK.hashCode() : 0);
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
    final AlarmAud other = (AlarmAud) obj;
    return Objects.equals(this.alarmAudPK, other.alarmAudPK);
  }
}

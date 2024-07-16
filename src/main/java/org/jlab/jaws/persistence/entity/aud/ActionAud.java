package org.jlab.jaws.persistence.entity.aud;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.envers.RevisionType;
import org.jlab.jaws.persistence.entity.ApplicationRevisionInfo;
import org.jlab.jaws.persistence.entity.Component;
import org.jlab.jaws.persistence.entity.Priority;
import org.jlab.smoothness.persistence.util.YnStringToBoolean;

/**
 * @author ryans
 */
@Entity
@Table(name = "ACTION_AUD", schema = "JAWS_OWNER")
public class ActionAud implements Serializable {

  private static final long serialVersionUID = 1L;
  @EmbeddedId protected ActionAudPK actionAudPK;

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

  @Size(max = 64)
  @Column(length = 64)
  private String name;

  @JoinColumn(name = "COMPONENT_ID", referencedColumnName = "COMPONENT_ID", nullable = false)
  @ManyToOne(optional = false)
  private Component component;

  @JoinColumn(name = "PRIORITY_ID", referencedColumnName = "PRIORITY_ID", nullable = false)
  @ManyToOne(optional = false)
  private Priority priority;

  @Column(name = "CORRECTIVE_ACTION")
  @Lob
  private String correctiveAction;

  @Column(name = "RATIONALE")
  @Lob
  private String rationale;

  @Column(name = "FILTERABLE", nullable = false, length = 1)
  @Convert(converter = YnStringToBoolean.class)
  private boolean filterable;

  @Column(name = "LATCHABLE", nullable = false, length = 1)
  @Convert(converter = YnStringToBoolean.class)
  private boolean latchable;

  @Column(name = "ON_DELAY_SECONDS", nullable = true, precision = 22, scale = 0)
  private BigInteger onDelaySeconds;

  @Column(name = "OFF_DELAY_SECONDS", nullable = true, precision = 22, scale = 0)
  private BigInteger offDelaySeconds;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Component getComponent() {
    return component;
  }

  public void setComponent(Component component) {
    this.component = component;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public String getCorrectiveAction() {
    return correctiveAction;
  }

  public void setCorrectiveAction(String correctiveAction) {
    this.correctiveAction = correctiveAction;
  }

  public String getRationale() {
    return rationale;
  }

  public void setRationale(String rationale) {
    this.rationale = rationale;
  }

  public boolean isFilterable() {
    return filterable;
  }

  public void setFilterable(boolean filterable) {
    this.filterable = filterable;
  }

  public boolean isLatchable() {
    return latchable;
  }

  public void setLatchable(boolean latchable) {
    this.latchable = latchable;
  }

  public BigInteger getOnDelaySeconds() {
    return onDelaySeconds;
  }

  public void setOnDelaySeconds(BigInteger onDelaySeconds) {
    this.onDelaySeconds = onDelaySeconds;
  }

  public BigInteger getOffDelaySeconds() {
    return offDelaySeconds;
  }

  public void setOffDelaySeconds(BigInteger offDelaySeconds) {
    this.offDelaySeconds = offDelaySeconds;
  }

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

  public ActionAudPK getActionAudPK() {
    return actionAudPK;
  }

  public void setActionAudPK(ActionAudPK systemAudPK) {
    this.actionAudPK = systemAudPK;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 17 * hash + (this.actionAudPK != null ? this.actionAudPK.hashCode() : 0);
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
    final ActionAud other = (ActionAud) obj;
    return Objects.equals(this.actionAudPK, other.actionAudPK);
  }
}

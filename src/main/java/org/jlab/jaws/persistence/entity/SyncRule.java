package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "SYNC_RULE", schema = "JAWS_OWNER")
public class SyncRule implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SyncRuleId", sequenceName = "SYNC_RULE_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SyncRuleId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "SYNC_RULE_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger syncRuleId;

  @JoinColumn(name = "ACTION_ID", referencedColumnName = "ACTION_ID", nullable = false)
  @ManyToOne(optional = false)
  private Action action;

  @Size(max = 32)
  @Column(length = 32)
  private String deployment;

  @Size(max = 4096)
  @Column(length = 4096)
  private String query;

  @Size(max = 512)
  @Column(name = "SCREEN_COMMAND", length = 512)
  private String screenCommand;

  @Size(max = 64)
  @Column(length = 64, nullable = true)
  private String pv;

  public BigInteger getSyncRuleId() {
    return syncRuleId;
  }

  public void setSyncRuleId(BigInteger syncRuleId) {
    this.syncRuleId = syncRuleId;
  }

  public Action getAction() {
    return action;
  }

  public void setAction(Action action) {
    this.action = action;
  }

  public @Size(max = 32) String getDeployment() {
    return deployment;
  }

  public void setDeployment(@Size(max = 32) String deployment) {
    this.deployment = deployment;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getScreenCommand() {
    return screenCommand;
  }

  public void setScreenCommand(String screenCommand) {
    this.screenCommand = screenCommand;
  }

  public String getPv() {
    return pv;
  }

  public void setPv(String pv) {
    this.pv = pv;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SyncRule)) return false;
    SyncRule that = (SyncRule) o;
    return Objects.equals(syncRuleId, that.syncRuleId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(syncRuleId);
  }
}

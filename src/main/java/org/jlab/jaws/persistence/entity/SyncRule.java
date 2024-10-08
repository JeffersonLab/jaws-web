package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

  @JoinColumn(name = "SYNC_SERVER_ID", referencedColumnName = "SYNC_SERVER_ID", nullable = false)
  @ManyToOne(optional = false)
  private SyncServer server;

  @Size(max = 128)
  @Column(name = "DESCRIPTION", length = 128)
  private String description;

  @Size(max = 4000)
  @Column(length = 4000)
  private String query;

  @Size(max = 4000)
  @Column(name = "PROPERTY_EXPRESSION", length = 4000)
  private String propertyExpression;

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

  public SyncServer getSyncServer() {
    return server;
  }

  public void setSyncServer(SyncServer server) {
    this.server = server;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPropertyExpression() {
    return propertyExpression;
  }

  public void setPropertyExpression(String propertyExpression) {
    this.propertyExpression = propertyExpression;
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

  public String getSearchURL() {
    String url = getHTMLURL();

    if (server.getExtraSearchQuery() != null && !server.getExtraSearchQuery().isBlank()) {
      url = url + "&" + server.getExtraSearchQuery();
    }

    return url;
  }

  public String getHTMLURL() {
    String url = server.getBaseUrl() + server.getSearchPath() + "?" + getQuery();

    if (propertyExpression != null && !propertyExpression.isBlank()) {
      url = url + "&Ex=" + URLEncoder.encode(propertyExpression, StandardCharsets.UTF_8);
    }

    return url;
  }

  public String[] getExpressionArray() {
    String[] tokens = new String[0];

    if (propertyExpression != null && !propertyExpression.isBlank()) {
      tokens = propertyExpression.split("&");
    }

    return tokens;
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

package org.jlab.jaws.persistence.entity.aud;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.envers.RevisionType;
import org.jlab.jaws.persistence.entity.*;

@Entity
@Table(name = "SYNC_RULE_AUD", schema = "JAWS_OWNER")
public class SyncRuleAud implements Serializable, Comparable<SyncRuleAud> {
  private static final long serialVersionUID = 1L;
  @EmbeddedId protected SyncRuleAudPK syncRuleAudPK;

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

  public SyncRuleAudPK getSyncRuleAudPK() {
    return syncRuleAudPK;
  }

  public void setSyncRuleAudPK(SyncRuleAudPK syncRuleAudPK) {
    this.syncRuleAudPK = syncRuleAudPK;
  }

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

  @Size(max = 64)
  @Column(name = "PRIMARY_ATTRIBUTE", length = 64)
  private String primaryAttribute;

  @Size(max = 64)
  @Column(name = "FOREIGN_ATTRIBUTE", length = 64)
  private String foreignAttribute;

  @Size(max = 4000)
  @Column(name = "FOREIGN_QUERY", length = 4000)
  private String foreignQuery;

  @Size(max = 4000)
  @Column(name = "FOREIGN_EXPRESSION", length = 4000)
  private String foreignExpression;

  @Size(max = 64)
  @Column(name = "ALARM_NAME", length = 64, nullable = false)
  private String alarmName;

  @Size(max = 512)
  @Column(name = "SCREEN_COMMAND", length = 512)
  private String screenCommand;

  @Size(max = 64)
  @Column(length = 64, nullable = true)
  private String pv;

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

  public String getPrimaryAttribute() {
    return primaryAttribute;
  }

  public void setPrimaryAttribute(String primaryAttribute) {
    this.primaryAttribute = primaryAttribute;
  }

  public String getForeignAttribute() {
    return foreignAttribute;
  }

  public void setForeignAttribute(String foreignAttribute) {
    this.foreignAttribute = foreignAttribute;
  }

  public String getForeignQuery() {
    return foreignQuery;
  }

  public void setForeignQuery(String foreignQuery) {
    this.foreignQuery = foreignQuery;
  }

  public String getForeignExpression() {
    return foreignExpression;
  }

  public void setForeignExpression(String foreignExpression) {
    this.foreignExpression = foreignExpression;
  }

  public String getAlarmName() {
    return alarmName;
  }

  public void setAlarmName(String alarmName) {
    this.alarmName = alarmName;
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

  public String getJoinSearchURL() {
    String url = getJoinHTMLURL();

    if (url != null
        && server.getExtraSearchQuery() != null
        && !server.getExtraSearchQuery().isBlank()) {
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

  public String getJoinHTMLURL() {
    String url = null;

    if (foreignQuery != null && !foreignQuery.isBlank()) {
      url = server.getBaseUrl() + server.getSearchPath() + "?" + foreignQuery;

      if (foreignExpression != null && !foreignExpression.isBlank()) {
        url = url + "&Ex=" + URLEncoder.encode(foreignExpression, StandardCharsets.UTF_8);
      }
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

  public String[] getForeignExpressionArray() {
    String[] tokens = new String[0];

    if (foreignExpression != null && !foreignExpression.isBlank()) {
      tokens = foreignExpression.split("&");
    }

    return tokens;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SyncRuleAud)) return false;
    SyncRuleAud that = (SyncRuleAud) o;
    return Objects.equals(this.getSyncRuleAudPK(), that.getSyncRuleAudPK());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.getSyncRuleAudPK());
  }

  @Override
  public int compareTo(SyncRuleAud o) {
    int result = action.getName().compareTo(o.getAction().getName());

    if (result == 0) {
      result = server.getSyncServerId().compareTo(o.getSyncServer().getSyncServerId());

      if (result == 0) {
        if (description != null && o.getDescription() != null) {
          result = description.compareTo(o.getDescription());
        }

        if (result == 0) {
          result =
              this.getSyncRuleAudPK()
                  .getSyncRuleId()
                  .compareTo(o.getSyncRuleAudPK().getSyncRuleId());
        }
      }
    }

    return result;
  }
}

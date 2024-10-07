package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "SYNC_SERVER", schema = "JAWS_OWNER")
public class SyncServer implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SyncServerId", sequenceName = "SYNC_SERVER_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SyncServerId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "SYNC_SERVER_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger syncServerId;

  @Size(max = 32)
  @Column(length = 32, nullable = false)
  private String name;

  @Size(max = 4000)
  @Column(name = "BASE_URL", length = 4000, nullable = false)
  private String baseUrl;

  @Size(max = 128)
  @Column(name = "ELEMENT_PATH", length = 128, nullable = false)
  private String elementPath;

  @Size(max = 128)
  @Column(name = "SEARCH_PATH", length = 128, nullable = false)
  private String searchPath;

  @Size(max = 4000)
  @Column(name = "EXTRA_SEARCH_QUERY", length = 4000)
  private String extraSearchQuery;

  public BigInteger getSyncServerId() {
    return syncServerId;
  }

  public void setSyncServerId(BigInteger syncServerId) {
    this.syncServerId = syncServerId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getElementPath() {
    return elementPath;
  }

  public void setElementPath(String elementPath) {
    this.elementPath = elementPath;
  }

  public String getSearchPath() {
    return searchPath;
  }

  public void setSearchPath(String searchPath) {
    this.searchPath = searchPath;
  }

  public String getExtraSearchQuery() {
    return extraSearchQuery;
  }

  public void setExtraSearchQuery(String extraSearchQuery) {
    this.extraSearchQuery = extraSearchQuery;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SyncServer)) return false;
    SyncServer that = (SyncServer) o;
    return Objects.equals(syncServerId, that.syncServerId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(syncServerId);
  }
}

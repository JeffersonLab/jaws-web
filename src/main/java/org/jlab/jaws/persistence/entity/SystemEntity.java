package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "SYSTEM", schema = "JAWS_OWNER")
public class SystemEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "SystemId", sequenceName = "SYSTEM_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SystemId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "SYSTEM_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger systemId;

  @JoinColumn(name = "TEAM_ID", referencedColumnName = "TEAM_ID", nullable = false)
  @ManyToOne(optional = false)
  private Team team;

  @Size(max = 64)
  @Column(length = 64)
  private String name;

  public BigInteger getSystemId() {
    return systemId;
  }

  public void setSystemId(BigInteger systemId) {
    this.systemId = systemId;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SystemEntity)) return false;
    SystemEntity entity = (SystemEntity) o;
    return Objects.equals(name, entity.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}

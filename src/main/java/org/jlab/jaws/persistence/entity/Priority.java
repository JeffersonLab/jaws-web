package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "PRIORITY", schema = "JAWS_OWNER")
public class Priority implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "PriorityId", sequenceName = "PRIORITY_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PriorityId")
  @Basic(optional = false)
  @NotNull
  @Column(name = "PRIORITY_ID", nullable = false, precision = 22, scale = 0)
  private BigInteger priorityId;

  @Size(max = 64)
  @Column(length = 64)
  private String name;

  public BigInteger getPriorityId() {
    return priorityId;
  }

  public void setPriorityId(BigInteger priorityId) {
    this.priorityId = priorityId;
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
    if (!(o instanceof Priority)) return false;
    Priority entity = (Priority) o;
    return Objects.equals(name, entity.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}

package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ActiveHistoryPK implements Serializable, Comparable<ActiveHistoryPK> {
  @NotNull
  @Size(max = 64)
  @Column(name = "NAME", length = 64, nullable = false)
  private String name;

  @Column(name = "ACTIVE_START", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  private Date activeStart;

  public ActiveHistoryPK() {}

  public ActiveHistoryPK(String name, Date activeStart) {
    this.name = name;
    this.activeStart = activeStart;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public @NotNull Date getActiveStart() {
    return activeStart;
  }

  public void setActiveStart(@NotNull Date activeStart) {
    this.activeStart = activeStart;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ActiveHistoryPK)) return false;
    ActiveHistoryPK that = (ActiveHistoryPK) o;
    return Objects.equals(name, that.name) && activeStart == that.activeStart;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, activeStart);
  }

  @Override
  public int compareTo(ActiveHistoryPK o) {
    int val = this.name.compareTo(o.getName());

    if (val == 0) { // Same name
      val = this.activeStart.compareTo(o.activeStart);
    }

    return val;
  }
}

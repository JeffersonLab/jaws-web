package org.jlab.jaws.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SuppressedHistoryPK implements Serializable, Comparable<SuppressedHistoryPK> {
  @NotNull
  @Size(max = 64)
  @Column(name = "NAME", length = 64, nullable = false)
  private String name;

  @Column(name = "SUPPRESSED_START", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @NotNull
  private Date suppressedStart;

  public SuppressedHistoryPK() {}

  public SuppressedHistoryPK(String name, Date suppressedStart) {
    this.name = name;
    this.suppressedStart = suppressedStart;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public @NotNull Date getSuppressedStart() {
    return suppressedStart;
  }

  public void setSuppressedStart(Date suppressedStart) {
    this.suppressedStart = suppressedStart;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SuppressedHistoryPK)) return false;
    SuppressedHistoryPK that = (SuppressedHistoryPK) o;
    return Objects.equals(name, that.name) && suppressedStart == that.suppressedStart;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, suppressedStart);
  }

  @Override
  public int compareTo(SuppressedHistoryPK o) {
    int val = this.name.compareTo(o.getName());

    if (val == 0) { // Same name
      val = this.suppressedStart.compareTo(o.suppressedStart);
    }

    return val;
  }
}

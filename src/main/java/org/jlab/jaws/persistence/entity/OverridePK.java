package org.jlab.jaws.persistence.entity;

import org.jlab.jaws.entity.OverriddenAlarmType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

public class OverridePK implements Serializable, Comparable<OverridePK> {
    @NotNull
    @Size(max = 64)
    @Column(name = "NAME", length = 64, nullable = false)
    private String name;
    @Size(max = 32)
    @Column(name = "TYPE", length = 32, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private OverriddenAlarmType type;

    public OverridePK() {}

    public OverridePK(String name, OverriddenAlarmType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OverriddenAlarmType getType() {
        return type;
    }

    public void setType(OverriddenAlarmType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OverridePK)) return false;
        OverridePK that = (OverridePK) o;
        return Objects.equals(name, that.name) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public int compareTo(OverridePK o) {
        int val = this.name.compareTo(o.getName());

        if (val == 0) { // Same name
            val = this.type.compareTo(o.type);
        }

        return val;
    }
}

package org.jlab.jaws.persistence.entity;

import org.jlab.jaws.entity.OverriddenAlarmType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

public class OverridePK implements Serializable {
    @NotNull
    @JoinColumn(name = "ALARM_ID", referencedColumnName = "ALARM_ID", nullable = false)
    @ManyToOne(optional = false)
    private Alarm alarm;
    @Size(max = 32)
    @Column(name = "OVERRIDE", length = 32, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private OverriddenAlarmType override;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OverridePK)) return false;
        OverridePK that = (OverridePK) o;
        return Objects.equals(alarm, that.alarm) && override == that.override;
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarm, override);
    }
}

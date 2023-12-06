package org.jlab.jaws.persistence.entity;

import org.jlab.smoothness.persistence.util.YnStringToBoolean;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Note: class is a reserved word in Java, so we use "AlarmClass" instead.
 */
@Entity
@Table(name = "CLASS", schema = "JAWS_OWNER")
public class AlarmClass implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "ClassId", sequenceName = "CLASS_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ClassId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "CLASS_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger classId;
    @Size(max = 64)
    @Column(length = 64)
    private String name;
    @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "CATEGORY_ID", nullable = false)
    @ManyToOne(optional = false)
    private Category category;
    @JoinColumn(name = "TEAM_ID", referencedColumnName = "TEAM_ID", nullable = false)
    @ManyToOne(optional = false)
    private Team team;
    @JoinColumn(name = "PRIORITY_ID", referencedColumnName = "PRIORITY_ID", nullable = false)
    @ManyToOne(optional = false)
    private Priority priority;
    @Column(name = "CORRECTIVE_ACTION")
    @Lob
    private String correctiveAction;
    @Column(name = "RATIONALE")
    @Lob
    private String rationale;
    @Column(name = "FILTERABLE", nullable = false, length = 1)
    @Convert(converter= YnStringToBoolean.class)
    private boolean filterable;
    @Column(name = "LATCHABLE", nullable = false, length = 1)
    @Convert(converter= YnStringToBoolean.class)
    private boolean latchable;
    @Column(name = "ON_DELAY_SECONDS", nullable = true, precision = 22, scale = 0)
    private BigInteger onDelaySeconds;
    @Column(name = "OFF_DELAY_SECONDS", nullable = true, precision = 22, scale = 0)
    private BigInteger offDelaySeconds;

    public BigInteger getClassId() {
        return classId;
    }

    public void setClassId(BigInteger classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(String correctiveAction) {
        this.correctiveAction = correctiveAction;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public void setFilterable(boolean filterable) {
        this.filterable = filterable;
    }

    public boolean isLatchable() {
        return latchable;
    }

    public void setLatchable(boolean latchable) {
        this.latchable = latchable;
    }

    public BigInteger getOnDelaySeconds() {
        return onDelaySeconds;
    }

    public void setOnDelaySeconds(BigInteger onDelaySeconds) {
        this.onDelaySeconds = onDelaySeconds;
    }

    public BigInteger getOffDelaySeconds() {
        return offDelaySeconds;
    }

    public void setOffDelaySeconds(BigInteger offDelaySeconds) {
        this.offDelaySeconds = offDelaySeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlarmClass)) return false;
        AlarmClass entity = (AlarmClass) o;
        return Objects.equals(name, entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

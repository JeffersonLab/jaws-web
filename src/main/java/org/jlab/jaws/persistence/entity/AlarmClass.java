package org.jlab.jaws.persistence.entity;

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

package org.jlab.jaws.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "LOCATION", schema = "JAWS_OWNER")
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "LocationId", sequenceName = "LOCATION_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LocationId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "LOCATION_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger locationId;
    @Size(max = 64)
    @Column(length = 64)
    private String name;
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "LOCATION_ID", nullable = true)
    @ManyToOne(optional = true)
    private Location parent;

    public BigInteger getLocationId() {
        return locationId;
    }

    public void setLocationId(BigInteger locationId) {
        this.locationId = locationId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getParent() {
        return parent;
    }

    public void setParent(Location parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location category = (Location) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

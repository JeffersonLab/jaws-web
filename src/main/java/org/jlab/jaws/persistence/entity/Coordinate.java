package org.jlab.jaws.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

@Entity
@Table(name = "COORDINATE", schema = "JAWS_OWNER")
public class Coordinate implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "CoordinateId", sequenceName = "COORDINATE_ID", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CoordinateId")
    @Basic(optional = false)
    @NotNull
    @Column(name = "COORDINATE_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger coordinateId;
    @Size(max = 64)
    @Column(length = 64)
    private String topic;
    @Column(name = "OFFSET", nullable = false, precision = 22, scale = 0)
    private BigInteger offset;
    public BigInteger getCoordinateId() {
        return coordinateId;
    }

    public void setCoordinateId(BigInteger coordinateId) {
        this.coordinateId = coordinateId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String name) {
        this.topic = name;
    }

    public BigInteger getOffset() {
        return offset;
    }

    public void setOffset(BigInteger offset) {
        this.offset = offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate entity = (Coordinate) o;
        return Objects.equals(topic, entity.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic);
    }
}

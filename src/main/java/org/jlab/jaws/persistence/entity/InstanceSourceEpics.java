package org.jlab.jaws.persistence.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "INSTANCE_SOURCE_EPICS", schema = "JAWS_OWNER")
public class InstanceSourceEpics implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "INSTANCE_ID", nullable = false, precision = 22, scale = 0)
    private BigInteger instanceId;
    @Size(max = 64)
    @Column(length = 64)
    private String pv;

    public BigInteger getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(BigInteger instanceId) {
        this.instanceId = instanceId;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstanceSourceEpics)) return false;
        InstanceSourceEpics that = (InstanceSourceEpics) o;
        return Objects.equals(instanceId, that.instanceId) && Objects.equals(pv, that.pv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId, pv);
    }
}

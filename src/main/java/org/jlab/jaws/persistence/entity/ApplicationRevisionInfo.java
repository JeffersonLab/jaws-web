package org.jlab.jaws.persistence.entity;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.jlab.smoothness.persistence.view.User;
import org.jlab.jaws.persistence.model.AuditedEntityChange;
import org.jlab.jaws.presentation.util.ApplicationRevisionInfoListener;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * An Envers entity auditing revision information record.
 *
 * @author ryans
 */
@Entity
@RevisionEntity(ApplicationRevisionInfoListener.class)
@Table(name = "APPLICATION_REVISION_INFO", schema = "JAWS_OWNER")
public class ApplicationRevisionInfo implements Serializable {
    @Id
    @GeneratedValue
    @RevisionNumber
    @Column(name = "REV", nullable = false)
    private long id;
    @RevisionTimestamp
    @Column(name = "REVTSTMP")
    private long ts;
    @Basic(optional = false)
    @Column(name = "USERNAME", length = 64)
    @Size(max = 64)
    private String username;
    @Basic(optional = false)
    @Column(name = "ADDRESS", length = 64)
    @Size(max = 64)
    private String address;
    @Transient
    private List<AuditedEntityChange> changeList;
    @Transient
    private User user;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (int) this.id;
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ApplicationRevisionInfo)) {
            return false;
        }

        return ((ApplicationRevisionInfo) o).getId() == this.getId();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return ts;
    }

    public void setTimestamp(long ts) {
        this.ts = ts;
    }

    public Date getRevisionDate() {
        return new Date(ts);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<AuditedEntityChange> getChangeList() {
        return changeList;
    }

    public void setChangeList(List<AuditedEntityChange> changeList) {
        this.changeList = changeList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

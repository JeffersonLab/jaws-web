package org.jlab.jaws.persistence.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "OVERRIDE", schema = "JAWS_OWNER")
public class AlarmOverride implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected OverridePK overridePK;
}

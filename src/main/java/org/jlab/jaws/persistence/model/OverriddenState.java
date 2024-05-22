package org.jlab.jaws.persistence.model;

import org.jlab.jaws.entity.AlarmState;
import org.jlab.jaws.entity.OverriddenAlarmType;

public enum OverriddenState {
    Disabled("Disabled (Normal)", OverriddenAlarmType.Disabled),
    Filtered("Filtered (Normal)", OverriddenAlarmType.Filtered),
    Masked("Masked (Normal)", OverriddenAlarmType.Masked),
    OnDelayed("On-Delayed (Normal)", OverriddenAlarmType.OnDelayed),
    OffDelayed("Off-Delayed (Active)", OverriddenAlarmType.OffDelayed),
    Shelved("Oneshot Shelved (Normal)", OverriddenAlarmType.Shelved),
    Latched("Latched (Active)", OverriddenAlarmType.Latched);

    private String label;
    private OverriddenAlarmType type;

    OverriddenState(String label, OverriddenAlarmType type) {
        this.label = label;
        this.type = type;
    }

    public String getLabel() {return label;}

    public OverriddenAlarmType getOverrideType() {
        return type;
    }
}

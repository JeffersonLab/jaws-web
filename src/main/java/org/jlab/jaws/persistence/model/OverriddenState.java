package org.jlab.jaws.persistence.model;

import org.jlab.jaws.entity.AlarmState;

public enum OverriddenState {
    NormalDisabled("Disabled (Normal)", AlarmState.NormalDisabled),
    NormalFiltered("Filtered (Normal)", AlarmState.NormalFiltered),
    NormalMasked("Masked (Normal)", AlarmState.NormalMasked),
    NormalOnDelayed("On-Delayed (Normal)", AlarmState.NormalOnDelayed),
    NormalOneShotShelved("Oneshot Shelved (Normal)", AlarmState.NormalOneShotShelved),
    NormalContinuousShelved("Continuous Shelved (Normal)", AlarmState.NormalContinuousShelved),
    ActiveOffDelayed("Off-Delayed (Active)", AlarmState.ActiveOffDelayed),
    ActiveLatched("Latched (Active)", AlarmState.ActiveLatched);

    private String label;
    private AlarmState state;

    OverriddenState(String label, AlarmState state) {
        this.label = label;
        this.state = state;
    }

    public String getLabel() {return label;}

    public AlarmState getAlarmState() {
        return state;
    }
}

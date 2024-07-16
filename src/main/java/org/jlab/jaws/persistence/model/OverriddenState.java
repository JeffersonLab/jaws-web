package org.jlab.jaws.persistence.model;

import org.jlab.jaws.entity.OverriddenAlarmType;

public enum OverriddenState {
  Disabled("Disabled (Normal)", OverriddenAlarmType.Disabled),
  Filtered("Filtered (Normal)", OverriddenAlarmType.Filtered),
  Masked("Masked (Normal)", OverriddenAlarmType.Masked),
  OnDelayed("OnDelayed (Normal)", OverriddenAlarmType.OnDelayed),
  OffDelayed("OffDelayed (Active)", OverriddenAlarmType.OffDelayed),
  Shelved("Shelved (Normal)", OverriddenAlarmType.Shelved),
  Latched("Latched (Active)", OverriddenAlarmType.Latched);

  private String label;
  private OverriddenAlarmType type;

  OverriddenState(String label, OverriddenAlarmType type) {
    this.label = label;
    this.type = type;
  }

  public String getLabel() {
    return label;
  }

  public OverriddenAlarmType getOverrideType() {
    return type;
  }
}

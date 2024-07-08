package org.jlab.jaws.persistence.model;

import java.util.ArrayList;
import java.util.List;
import org.jlab.jaws.entity.AlarmState;

public enum BinaryState {
  Active,
  Normal;

  public static List<AlarmState> getActiveList() {
    List<AlarmState> list = new ArrayList<>();

    list.add(AlarmState.Active);
    list.add(AlarmState.ActiveLatched);
    list.add(AlarmState.ActiveOffDelayed);

    return list;
  }

  public static List<AlarmState> getNormalList() {
    List<AlarmState> list = new ArrayList<>();

    list.add(AlarmState.Normal);
    list.add(AlarmState.NormalDisabled);
    list.add(AlarmState.NormalFiltered);
    list.add(AlarmState.NormalMasked);
    list.add(AlarmState.NormalOnDelayed);
    list.add(AlarmState.NormalContinuousShelved);
    list.add(AlarmState.NormalOneShotShelved);

    return list;
  }

  public static BinaryState fromAlarmState(AlarmState state) {
    if (getActiveList().contains(state)) {
      return BinaryState.Active;
    } else {
      return BinaryState.Normal;
    }
  }
}

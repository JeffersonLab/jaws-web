package org.jlab.jaws.persistence.model;

import org.jlab.jaws.entity.AlarmState;

import java.util.ArrayList;
import java.util.List;

public enum SuppressedState {
  SUPPRESSED,
  NOT_SUPPRESSED;

  public static List<AlarmState> getNotSuppressedList() {
    List<AlarmState> list = new ArrayList<>();

    list.add(AlarmState.Normal);
    list.add(AlarmState.Active);
    list.add(AlarmState.ActiveLatched);
    list.add(AlarmState.ActiveOffDelayed);

    return list;
  }

  public static List<AlarmState> getSuppressedList() {
    List<AlarmState> list = new ArrayList<>();

    list.add(AlarmState.NormalDisabled);
    list.add(AlarmState.NormalFiltered);
    list.add(AlarmState.NormalMasked);
    list.add(AlarmState.NormalOnDelayed);
    list.add(AlarmState.NormalContinuousShelved);
    list.add(AlarmState.NormalOneShotShelved);

    return list;
  }

  public static SuppressedState fromAlarmState(AlarmState state) {
    if (getNotSuppressedList().contains(state)) {
      return SuppressedState.NOT_SUPPRESSED;
    } else {
      return SuppressedState.SUPPRESSED;
    }
  }
}

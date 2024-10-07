package org.jlab.jaws.persistence.model;

import java.util.ArrayList;
import java.util.List;
import org.jlab.jaws.persistence.entity.AlarmEntity;

public class AlarmSyncDiff {
  public final List<AlarmEntity> removeList = new ArrayList<>();
  public final List<AlarmEntity> addList = new ArrayList<>();
  public final List<AlarmEntity> updateList = new ArrayList<>();
  public final List<AlarmEntity> matchList = new ArrayList<>();

  public List<AlarmEntity> getRemoveList() {
    return removeList;
  }

  public List<AlarmEntity> getAddList() {
    return addList;
  }

  public List<AlarmEntity> getUpdateList() {
    return updateList;
  }

  public List<AlarmEntity> getMatchList() {
    return matchList;
  }
}

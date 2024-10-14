package org.jlab.jaws.persistence.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import org.jlab.jaws.persistence.entity.SyncRule;

public class RuleSet {
  private final String name;
  private final TreeSet<SyncRule> ts = new TreeSet<>();

  public RuleSet(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void addSyncRule(SyncRule syncRule) {
    ts.add(syncRule);
  }

  public List<SyncRule> getRuleList() {
    return new ArrayList<>(ts);
  }
}

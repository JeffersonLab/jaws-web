package org.jlab.jaws.persistence.model;

import java.util.ArrayList;
import java.util.List;

public class EntityModel {
  private List<FieldDefinition> keyFields = new ArrayList<>();
  private List<FieldDefinition> valueFields = new ArrayList<>();
  private List<String> tableColumns;

  public List<FieldDefinition> getKeyFields() {
    return keyFields;
  }

  public void setKeyFields(List<FieldDefinition> keyFields) {
    this.keyFields = keyFields;
  }

  public List<FieldDefinition> getValueFields() {
    return valueFields;
  }

  public void setValueFields(List<FieldDefinition> valueFields) {
    this.valueFields = valueFields;
  }

  public List<String> getTableColumns() {
    return tableColumns;
  }

  public void setTableColumns(List<String> tableColumns) {
    this.tableColumns = tableColumns;
  }
}

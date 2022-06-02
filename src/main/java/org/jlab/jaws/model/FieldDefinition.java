package org.jlab.jaws.model;

public class FieldDefinition {
    private String name;

    private FieldType type;
    private boolean key;
    private boolean inTable;

    public FieldDefinition(String name, FieldType type, boolean key, boolean inTable) {
        this.name = name;
        this.key = key;
        this.inTable = inTable;
    }

    public String getName() {
        return name;
    }

    public FieldType getType() {
        return type;
    }

    public boolean isKey() {
        return key;
    }

    public boolean isInTable() {
        return inTable;
    }
}

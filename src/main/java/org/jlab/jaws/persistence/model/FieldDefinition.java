package org.jlab.jaws.persistence.model;

public class FieldDefinition {
    private String name;

    private FieldType type;

    public FieldDefinition(String name, FieldType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public FieldType getType() {
        return type;
    }
}

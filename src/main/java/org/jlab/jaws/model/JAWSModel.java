package org.jlab.jaws.model;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JAWSModel {
    private List<FieldDefinition> alarmFields = new ArrayList<>();
    private List<FieldDefinition> activationFields = new ArrayList<>();
    private List<FieldDefinition> categoryFields = new ArrayList<>();
    private List<FieldDefinition> classFields = new ArrayList<>();
    private List<FieldDefinition> instanceFields = new ArrayList<>();
    private List<FieldDefinition> locationFields = new ArrayList<>();
    private List<FieldDefinition> notificationFields = new ArrayList<>();
    private List<FieldDefinition> overrideFields = new ArrayList<>();
    private List<FieldDefinition> registrationFields = new ArrayList<>();

    public JAWSModel() {
        alarmFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        alarmFields.add(new FieldDefinition("state", FieldType.STRING, false, true));

        activationFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        activationFields.add(new FieldDefinition("error", FieldType.STRING, false, true));
        activationFields.add(new FieldDefinition("note", FieldType.STRING, false, false));
        activationFields.add(new FieldDefinition("sevr", FieldType.STRING, false, false));
        activationFields.add(new FieldDefinition("stat", FieldType.STRING, false, false));

        categoryFields.add(new FieldDefinition("name", FieldType.STRING, true, true));

        classFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        classFields.add(new FieldDefinition("category", FieldType.STRING, false, true));
        classFields.add(new FieldDefinition("priority", FieldType.ENUM, false, true));
        classFields.add(new FieldDefinition("rationale", FieldType.MARKDOWN, false, false));
        classFields.add(new FieldDefinition("action", FieldType.MARKDOWN, false, false));
        classFields.add(new FieldDefinition("latching", FieldType.BOOLEAN, false, false));
        classFields.add(new FieldDefinition("filterable", FieldType.BOOLEAN, false, false));
        classFields.add(new FieldDefinition("ondelay", FieldType.NUMBER,false, false));
        classFields.add(new FieldDefinition("offdelay", FieldType.NUMBER, false, false));
        classFields.add(new FieldDefinition("contact", FieldType.STRING, false, true));

        instanceFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        instanceFields.add(new FieldDefinition("class", FieldType.STRING, false, true));
        instanceFields.add(new FieldDefinition("location", FieldType.MULTI_ENUM, false, true));
        instanceFields.add(new FieldDefinition("epicspv", FieldType.STRING, false, true));
        instanceFields.add(new FieldDefinition("maskedby", FieldType.STRING, false, false));
        instanceFields.add(new FieldDefinition("screencommand", FieldType.STRING, false, false));

        locationFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        locationFields.add(new FieldDefinition("parent", FieldType.STRING, false, true));

        notificationFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        notificationFields.add(new FieldDefinition("state", FieldType.STRING, false, true));

        overrideFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        overrideFields.add(new FieldDefinition("comments", FieldType.STRING, false, false));
        overrideFields.add(new FieldDefinition("expiration", FieldType.UNIX_TIMESTAMP, false, false));
        overrideFields.add(new FieldDefinition("filtername", FieldType.STRING, false, false));
        overrideFields.add(new FieldDefinition("oneshot", FieldType.BOOLEAN, false, false));
        overrideFields.add(new FieldDefinition("reason", FieldType.ENUM, false, false));

        registrationFields.add(new FieldDefinition("name", FieldType.STRING, true, true));
        registrationFields.add(new FieldDefinition("category", FieldType.STRING, false, true));
        registrationFields.add(new FieldDefinition("class", FieldType.STRING, false, true));
        registrationFields.add(new FieldDefinition("location", FieldType.MULTI_ENUM, false, true));
        registrationFields.add(new FieldDefinition("priority", FieldType.ENUM, false, true));
        registrationFields.add(new FieldDefinition("rationale", FieldType.MARKDOWN, false, false));
        registrationFields.add(new FieldDefinition("action", FieldType.MARKDOWN, false, false));
        registrationFields.add(new FieldDefinition("latching", FieldType.BOOLEAN, false, false));
        registrationFields.add(new FieldDefinition("filterable", FieldType.BOOLEAN, false, false));
        registrationFields.add(new FieldDefinition("ondelay", FieldType.NUMBER, false, false));
        registrationFields.add(new FieldDefinition("offdelay", FieldType.NUMBER, false, false));
        registrationFields.add(new FieldDefinition("contact", FieldType.STRING, false, true));
        registrationFields.add(new FieldDefinition("epicspv", FieldType.STRING, false, true));
        registrationFields.add(new FieldDefinition("maskedby", FieldType.STRING, false, false));
        registrationFields.add(new FieldDefinition("screencommand", FieldType.STRING, false, false));
    }

    public List<FieldDefinition> getAlarmFields() {
        return alarmFields;
    }

    public List<FieldDefinition> getActivationFields() {
        return activationFields;
    }

    public List<FieldDefinition> getCategoryFields() {
        return categoryFields;
    }

    public List<FieldDefinition> getClassFields() {
        return classFields;
    }

    public List<FieldDefinition> getInstanceFields() {
        return instanceFields;
    }

    public List<FieldDefinition> getLocationFields() {
        return locationFields;
    }

    public List<FieldDefinition> getNotificationFields() {
        return notificationFields;
    }

    public List<FieldDefinition> getOverrideFields() {
        return overrideFields;
    }

    public List<FieldDefinition> getRegistrationFields() {
        return registrationFields;
    }
}

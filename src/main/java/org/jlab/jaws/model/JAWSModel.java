package org.jlab.jaws.model;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class JAWSModel {
    private EntityModel alarmModel = new EntityModel();
    private EntityModel activationModel = new EntityModel();
    private EntityModel categoryModel = new EntityModel();
    private EntityModel classModel = new EntityModel();
    private EntityModel instanceModel = new EntityModel();
    private EntityModel locationModel = new EntityModel();
    private EntityModel notificationModel = new EntityModel();
    private EntityModel overrideModel = new EntityModel();
    private EntityModel registrationModel = new EntityModel();

    public JAWSModel() {
        List<FieldDefinition> keyFields;
        List<FieldDefinition> valueFields;

        activationModel.setTableColumns(Arrays.asList(new String[]{"name", "type", "error"}));
        keyFields = new ArrayList<>();
        keyFields.add(new FieldDefinition("name", FieldType.STRING));
        activationModel.setKeyFields(keyFields);
        valueFields = new ArrayList<>();
        valueFields.add(new FieldDefinition("type", FieldType.STRING));
        valueFields.add(new FieldDefinition("error", FieldType.STRING));
        valueFields.add(new FieldDefinition("note", FieldType.STRING));
        valueFields.add(new FieldDefinition("sevr", FieldType.STRING));
        valueFields.add(new FieldDefinition("stat", FieldType.STRING));
        activationModel.setValueFields(valueFields);

        categoryModel.setTableColumns(Arrays.asList(new String[]{"name"}));
        keyFields = new ArrayList<>();
        keyFields.add(new FieldDefinition("name", FieldType.STRING));
        categoryModel.setKeyFields(keyFields);
        valueFields = new ArrayList<>();
        categoryModel.setValueFields(valueFields);

        classModel.setTableColumns(Arrays.asList(new String[]{"name", "category", "priority", "contact"}));
        keyFields = new ArrayList<>();
        keyFields.add(new FieldDefinition("name", FieldType.STRING));
        classModel.setKeyFields(keyFields);
        valueFields = new ArrayList<>();
        valueFields.add(new FieldDefinition("category", FieldType.STRING));
        valueFields.add(new FieldDefinition("priority", FieldType.ENUM));
        valueFields.add(new FieldDefinition("rationale", FieldType.MARKDOWN));
        valueFields.add(new FieldDefinition("action", FieldType.MARKDOWN));
        valueFields.add(new FieldDefinition("latchable", FieldType.BOOLEAN));
        valueFields.add(new FieldDefinition("filterable", FieldType.BOOLEAN));
        valueFields.add(new FieldDefinition("ondelay", FieldType.NUMBER));
        valueFields.add(new FieldDefinition("offdelay", FieldType.NUMBER));
        valueFields.add(new FieldDefinition("contact", FieldType.STRING));
        classModel.setValueFields(valueFields);

        instanceModel.setTableColumns(Arrays.asList(new String[]{"name", "class", "location", "epicspv"}));
        keyFields = new ArrayList<>();
        keyFields.add(new FieldDefinition("name", FieldType.STRING));
        instanceModel.setKeyFields(keyFields);
        valueFields = new ArrayList<>();
        valueFields.add(new FieldDefinition("class", FieldType.STRING));
        valueFields.add(new FieldDefinition("location", FieldType.MULTI_ENUM));
        valueFields.add(new FieldDefinition("epicspv", FieldType.STRING));
        valueFields.add(new FieldDefinition("maskedby", FieldType.STRING));
        valueFields.add(new FieldDefinition("screencommand", FieldType.STRING));
        instanceModel.setValueFields(valueFields);

        locationModel.setTableColumns(Arrays.asList(new String[]{"name", "parent"}));
        keyFields = new ArrayList<>();
        keyFields.add(new FieldDefinition("name", FieldType.STRING));
        locationModel.setKeyFields(keyFields);
        valueFields = new ArrayList<>();
        valueFields.add(new FieldDefinition("parent", FieldType.STRING));
        locationModel.setValueFields(valueFields);

        overrideModel.setTableColumns(Arrays.asList(new String[]{"name"}));
        keyFields = new ArrayList<>();
        keyFields.add(new FieldDefinition("name", FieldType.STRING));
        overrideModel.setKeyFields(keyFields);
        valueFields = new ArrayList<>();
        valueFields.add(new FieldDefinition("comments", FieldType.STRING));
        valueFields.add(new FieldDefinition("expiration", FieldType.UNIX_TIMESTAMP));
        valueFields.add(new FieldDefinition("filtername", FieldType.STRING));
        valueFields.add(new FieldDefinition("oneshot", FieldType.BOOLEAN));
        valueFields.add(new FieldDefinition("reason", FieldType.ENUM));
        overrideModel.setValueFields(valueFields);


        // Effective entities

        notificationModel.setTableColumns(Arrays.asList(new String[]{"name", "state"}));
        keyFields = new ArrayList<>();
        keyFields.add(new FieldDefinition("name", FieldType.STRING));
        notificationModel.setKeyFields(keyFields);
        valueFields = new ArrayList<>();
        valueFields.add(new FieldDefinition("state", FieldType.STRING));
        valueFields.add(new FieldDefinition("disabled_comments", FieldType.STRING));
        valueFields.add(new FieldDefinition("ondelayed_expiration", FieldType.UNIX_TIMESTAMP));
        valueFields.add(new FieldDefinition("offdelayed_expiration", FieldType.UNIX_TIMESTAMP));
        valueFields.add(new FieldDefinition("filtered_filtername", FieldType.STRING));
        valueFields.add(new FieldDefinition("shelved_oneshot", FieldType.BOOLEAN));
        valueFields.add(new FieldDefinition("shelved_reason", FieldType.ENUM));
        valueFields.add(new FieldDefinition("shelved_comments", FieldType.STRING));
        valueFields.add(new FieldDefinition("latched", FieldType.BOOLEAN));
        valueFields.add(new FieldDefinition("masked", FieldType.BOOLEAN));
        valueFields.addAll(activationModel.getValueFields());
        notificationModel.setValueFields(valueFields);

        registrationModel.setTableColumns(Arrays.asList(new String[]{"name", "category", "class", "location", "priority", "contact", "epicspv"}));
        keyFields = new ArrayList<>();
        keyFields.add(new FieldDefinition("name", FieldType.STRING));
        registrationModel.setKeyFields(keyFields);
        valueFields = new ArrayList<>();
        valueFields.addAll(classModel.getValueFields());
        valueFields.addAll(instanceModel.getValueFields());
        registrationModel.setValueFields(valueFields);

        alarmModel.setTableColumns(Arrays.asList(new String[]{"name", "state", "priority"}));
        keyFields = new ArrayList<>();
        keyFields.add(new FieldDefinition("name", FieldType.STRING));
        alarmModel.setKeyFields(keyFields);
        valueFields = new ArrayList<>();
        valueFields.addAll(registrationModel.getValueFields());
        valueFields.addAll(notificationModel.getValueFields());
        alarmModel.setValueFields(valueFields);
    }

    public EntityModel getAlarmModel() {
        return alarmModel;
    }

    public EntityModel getActivationModel() {
        return activationModel;
    }

    public EntityModel getCategoryModel() {
        return categoryModel;
    }

    public EntityModel getClassModel() {
        return classModel;
    }

    public EntityModel getInstanceModel() {
        return instanceModel;
    }

    public EntityModel getLocationModel() {
        return locationModel;
    }

    public EntityModel getNotificationModel() {
        return notificationModel;
    }

    public EntityModel getOverrideModel() {
        return overrideModel;
    }

    public EntityModel getRegistrationModel() {
        return registrationModel;
    }
}

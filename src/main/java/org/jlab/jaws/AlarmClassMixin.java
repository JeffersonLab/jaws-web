package org.jlab.jaws;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jlab.jaws.entity.AlarmCategory;
import org.jlab.jaws.entity.AlarmLocation;
import org.jlab.jaws.entity.AlarmPriority;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
interface AlarmClassMixin {
    @JsonProperty
    String getName();

    @JsonProperty
    AlarmLocation getLocation();

    @JsonProperty
    AlarmCategory getCategory();

    @JsonProperty
    AlarmPriority getPriority();

    @JsonProperty
    String getRationale();

    @JsonProperty
    String getCorrectiveaction();

    @JsonProperty
    String getPointofcontactusername();

    @JsonProperty
    boolean getLatching();

    @JsonProperty
    boolean getFilterable();

    @JsonProperty
    Long getOndelayseconds();

    @JsonProperty
    Long getOffdelayseconds();

    @JsonProperty
    String getMaskedby();

    @JsonProperty
    String getScreenpath();
}
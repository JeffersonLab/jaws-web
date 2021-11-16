package org.jlab.jaws;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jlab.jaws.entity.*;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
interface AlarmRegistrationMixin {
    @JsonProperty
    String getName();

    @JsonProperty("class")
    String getClass$();

    @JsonProperty
    Object getProducer();

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
    Boolean getLatching();

    @JsonProperty
    Boolean getFilterable();

    @JsonProperty
    Long getOndelayseconds();

    @JsonProperty
    Long getOffdelayseconds();

    @JsonProperty
    String getMaskedby();

    @JsonProperty
    String getScreenpath();
}
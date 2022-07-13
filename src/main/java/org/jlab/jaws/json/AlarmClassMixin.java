package org.jlab.jaws.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jlab.jaws.entity.AlarmPriority;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public
interface AlarmClassMixin {
    @JsonProperty
    String getCategory();

    @JsonProperty
    AlarmPriority getPriority();

    @JsonProperty
    String getRationale();

    @JsonProperty
    String getCorrectiveaction();

    @JsonProperty
    String getPointofcontactusername();

    @JsonProperty
    boolean getLatchable();

    @JsonProperty
    boolean getFilterable();

    @JsonProperty
    Long getOndelayseconds();

    @JsonProperty
    Long getOffdelayseconds();
}
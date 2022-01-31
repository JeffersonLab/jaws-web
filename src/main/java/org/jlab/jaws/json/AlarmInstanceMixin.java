package org.jlab.jaws.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public
interface AlarmInstanceMixin {
    @JsonProperty
    String getName();

    @JsonProperty("class")
    String getClass$();

    @JsonProperty
    Object getProducer();

    @JsonProperty
    String[] getLocation();

    @JsonProperty
    String getMaskedby();

    @JsonProperty
    String getScreencommand();
}
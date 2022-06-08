package org.jlab.jaws.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public
interface AlarmInstanceMixin {
    @JsonProperty("class")
    String getClass$();

    @JsonProperty
    Object getProducer();

    @JsonProperty
    List<String> getLocation();

    @JsonProperty
    String getMaskedby();

    @JsonProperty
    String getScreencommand();
}
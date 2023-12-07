package org.jlab.jaws.persistence.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.jlab.jaws.entity.AlarmClass;
import org.jlab.jaws.entity.AlarmInstance;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public
interface EffectiveRegistrationMixin {
    @JsonProperty
    @JsonUnwrapped
    AlarmInstance getInstance();

    @JsonProperty
    @JsonUnwrapped
    AlarmClass getClass$();
}
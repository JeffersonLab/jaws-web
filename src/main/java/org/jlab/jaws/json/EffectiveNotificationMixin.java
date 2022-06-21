package org.jlab.jaws.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.jlab.jaws.entity.*;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public
interface EffectiveNotificationMixin {
    @JsonProperty
    @JsonUnwrapped
    AlarmActivationUnion getActivation();

    @JsonProperty
    @JsonUnwrapped
    AlarmOverrideSet getOverrides();

    @JsonProperty
    AlarmState getState();
}
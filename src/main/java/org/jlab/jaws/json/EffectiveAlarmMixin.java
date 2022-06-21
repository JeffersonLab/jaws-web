package org.jlab.jaws.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.jlab.jaws.entity.EffectiveActivation;
import org.jlab.jaws.entity.EffectiveRegistration;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public
interface EffectiveAlarmMixin {
    @JsonProperty
    @JsonUnwrapped
    EffectiveActivation getNotification();

    @JsonProperty
    @JsonUnwrapped
    EffectiveRegistration getRegistration();
}
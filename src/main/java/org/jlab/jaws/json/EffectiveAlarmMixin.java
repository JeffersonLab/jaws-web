package org.jlab.jaws.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jlab.jaws.entity.EffectiveNotification;
import org.jlab.jaws.entity.EffectiveRegistration;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public
interface EffectiveAlarmMixin {
    @JsonProperty
    EffectiveNotification getNotification();

    @JsonProperty
    EffectiveRegistration getRegistration();
}
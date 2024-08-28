package org.jlab.jaws.persistence.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.jlab.jaws.entity.Alarm;
import org.jlab.jaws.entity.AlarmAction;

@JsonAutoDetect(
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public interface EffectiveRegistrationMixin {
  @JsonProperty
  @JsonUnwrapped
  Alarm getInstance();

  @JsonProperty
  @JsonUnwrapped
  AlarmAction getAction();
}

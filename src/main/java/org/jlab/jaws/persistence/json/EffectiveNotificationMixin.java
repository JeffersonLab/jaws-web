package org.jlab.jaws.persistence.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jlab.jaws.entity.*;

@JsonAutoDetect(
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public interface EffectiveNotificationMixin {
  @JsonProperty
  AlarmActivationUnion getActivation();

  @JsonProperty
  AlarmOverrideSet getOverrides();

  @JsonProperty
  AlarmState getState();
}

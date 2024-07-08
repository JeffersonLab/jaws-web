package org.jlab.jaws.persistence.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jlab.jaws.entity.*;

@JsonAutoDetect(
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public interface AlarmOverrideSetMixin {
  @JsonProperty
  DisabledOverride getDisabled();

  @JsonProperty
  FilteredOverride getFiltered();

  @JsonProperty
  LatchedOverride getLatched();

  @JsonProperty
  MaskedOverride getMasked();

  @JsonProperty
  OnDelayedOverride getOnDelayed();

  @JsonProperty
  OffDelayedOverride getOffDelayed();

  @JsonProperty
  ShelvedOverride getShelved();
}

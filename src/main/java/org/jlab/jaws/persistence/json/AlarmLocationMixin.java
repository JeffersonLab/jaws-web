package org.jlab.jaws.persistence.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public interface AlarmLocationMixin {
  @JsonProperty
  String getName();

  @JsonProperty
  String getParent();
}

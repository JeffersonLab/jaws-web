package org.jlab.jaws.persistence.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jlab.jaws.entity.AlarmPriority;

@JsonAutoDetect(
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public interface AlarmActionMixin {
  @JsonProperty
  String getSystem();

  @JsonProperty
  AlarmPriority getPriority();

  @JsonProperty
  String getRationale();

  @JsonProperty
  String getCorrectiveaction();

  @JsonProperty
  boolean getLatchable();

  @JsonProperty
  boolean getFilterable();

  @JsonProperty
  Long getOndelayseconds();

  @JsonProperty
  Long getOffdelayseconds();
}

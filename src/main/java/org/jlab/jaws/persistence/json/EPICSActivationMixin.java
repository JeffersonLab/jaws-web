package org.jlab.jaws.persistence.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jlab.jaws.entity.SevrEnum;
import org.jlab.jaws.entity.StatEnum;

@JsonAutoDetect(
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public interface EPICSActivationMixin {
  @JsonProperty
  SevrEnum getSevr();

  @JsonProperty
  StatEnum getStat();
}

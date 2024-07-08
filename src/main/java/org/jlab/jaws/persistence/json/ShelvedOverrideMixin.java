package org.jlab.jaws.persistence.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jlab.jaws.entity.ShelvedReason;

@JsonAutoDetect(
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public interface ShelvedOverrideMixin {
  @JsonProperty
  String getComments();

  @JsonProperty
  long getExpiration();

  @JsonProperty
  boolean getOneshot();

  @JsonProperty
  ShelvedReason getReason();
}

package org.jlab.jaws.persistence.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonAutoDetect(
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public interface AlarmMixin {
  @JsonProperty
  String getAction();

  @JsonProperty
  Object getSource();

  @JsonProperty
  String getDevice();

  @JsonProperty
  List<String> getLocation();

  @JsonProperty
  String getManagedby();

  @JsonProperty
  String getMaskedby();

  @JsonProperty
  String getScreencommand();
}

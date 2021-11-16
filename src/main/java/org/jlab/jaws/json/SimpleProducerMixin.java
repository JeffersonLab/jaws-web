package org.jlab.jaws.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jlab.jaws.entity.AlarmCategory;
import org.jlab.jaws.entity.AlarmLocation;
import org.jlab.jaws.entity.AlarmPriority;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public
interface SimpleProducerMixin {
}
package io.github.opendonationassistant.reel;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class WidgetProperty {
  private String name;
  private String displayName;
  private String type;
  private Object value;

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getDisplayName() {
    return displayName;
  }
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public Object getValue() {
    return value;
  }
  public void setValue(Object value) {
    this.value = value;
  }
  @Override
  public String toString() {
    return "{\"_type\"=\"WidgetProperty\",\"name\"=\"" + name + "\", displayName\"=\"" + displayName + "\", type\"=\""
        + type + "\", value\"=\"" + value + "}";
  }
}

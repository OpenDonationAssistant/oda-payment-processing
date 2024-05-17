package io.github.opendonationassistant.config;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ConfigPutCommand {
  private String ownerId;
  private String name;
  private String key;
  private Object value;
  public String getOwnerId() {
    return ownerId;
  }
  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public Object getValue() {
    return value;
  }
  public void setValue(Object value) {
    this.value = value;
  }
}

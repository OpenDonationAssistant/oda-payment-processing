package io.github.opendonationassistant.art;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class OperationDescription {

  private String id;

  public String getId() {
    return id;
  }

  public OperationDescription(String id) {
    this.id = id;
  }
}

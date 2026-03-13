package io.github.opendonationassistant.art;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Operation(boolean done, OperationResponse response) {
  @Serdeable
  public static record OperationResponse(String image) {}
}

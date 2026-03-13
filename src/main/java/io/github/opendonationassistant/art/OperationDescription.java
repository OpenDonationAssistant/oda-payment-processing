package io.github.opendonationassistant.art;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record OperationDescription(String id) {}

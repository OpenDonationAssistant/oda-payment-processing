package io.github.opendonationassistant.history;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Attachment(String id, String url, String title) {}

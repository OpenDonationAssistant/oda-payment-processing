package io.github.opendonationassistant.reel.repository;

import org.jspecify.annotations.Nullable;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("reel_link")
public record ReelLink(
  @Id String id,
  String reelId,
  @Nullable String originId,
  String source
) {}

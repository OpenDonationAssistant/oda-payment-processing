package io.github.opendonationassistant.donaton.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nullable;

@Serdeable
@MappedEntity("donaton_link")
public record DonatonLink(
  @Id String id,
  String donatonId,
  @Nullable String originId,
  String source
) {}

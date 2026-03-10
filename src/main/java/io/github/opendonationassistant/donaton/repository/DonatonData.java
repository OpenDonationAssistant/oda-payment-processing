package io.github.opendonationassistant.donaton.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.sourcegen.annotations.Wither;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Serdeable
@MappedEntity("donaton")
@Wither
public record DonatonData(
  @Id String id,
  String recipientId,
  Instant endDate,
  @MappedProperty(type = DataType.JSON)
  Map<String, BigDecimal> secondsPerDonation,
  Boolean enabled
) implements DonatonDataWither {
  public Map<String, BigDecimal> secondsPerDonation() {
    return secondsPerDonation == null ? Map.of() : secondsPerDonation;
  }
}

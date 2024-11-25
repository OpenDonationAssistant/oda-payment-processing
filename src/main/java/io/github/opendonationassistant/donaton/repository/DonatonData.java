package io.github.opendonationassistant.donaton.repository;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import io.micronaut.jackson.ObjectMapperFactory;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Serdeable
@MappedEntity("donaton")
public class DonatonData {

  @Id
  private String id;

  private String recipientId;
  private Instant endDate;

  @MappedProperty(type = DataType.JSON)
  private Map<String, BigDecimal> secondsPerDonation;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }

  public Instant getEndDate() {
    return endDate;
  }

  public void setEndDate(Instant endDate) {
    this.endDate = endDate;
  }

  public Map<String, BigDecimal> getSecondsPerDonation() {
    return secondsPerDonation;
  }

  public void setSecondsPerDonation(
    Map<String, BigDecimal> secondsPerDonation
  ) {
    this.secondsPerDonation = secondsPerDonation;
  }

  @Override
  public String toString() {
    try {
      return ObjectMapper.getDefault().writeValueAsString(this);
    } catch (Exception e) {
      return "Can't serializate DonatonData: " + e.getMessage();
    }
  }
}

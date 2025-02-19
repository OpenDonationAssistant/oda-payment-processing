package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.commons.Amount;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity("goal")
public class GoalData {

  @Id
  private String id;

  private String recipientId;
  private String widgetId;
  private String briefDescription;
  private String fullDescription;
  private Amount accumulatedAmount;
  private Amount requiredAmount;

  @MappedProperty("isdefault")
  private boolean isDefault = false;

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

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public String getBriefDescription() {
    return briefDescription;
  }

  public void setBriefDescription(String briefDescription) {
    this.briefDescription = briefDescription;
  }

  public String getFullDescription() {
    return fullDescription;
  }

  public void setFullDescription(String fullDescription) {
    this.fullDescription = fullDescription;
  }

  public Amount getAccumulatedAmount() {
    return accumulatedAmount;
  }

  public void setAccumulatedAmount(Amount accumulatedAmount) {
    this.accumulatedAmount = accumulatedAmount;
  }

  public Amount getRequiredAmount() {
    return requiredAmount;
  }

  public void setRequiredAmount(Amount requiredAmount) {
    this.requiredAmount = requiredAmount;
  }

  @MappedProperty("isdefault")
  public boolean isDefault() {
    return isDefault;
  }

  @MappedProperty("isdefault")
  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  @Override
  public String toString() {
    try {
      return ObjectMapper.getDefault().writeValueAsString(this);
    } catch (Exception e) {
      return "Can't serialize DonatonData: " + e.getMessage();
    }
  }
}

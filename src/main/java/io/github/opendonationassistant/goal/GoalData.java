package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.reel.Amount;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
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


  @Override
  public String toString() {
    return (
      "{\"_type\"=\"GoalData\",\"id\"=\"" +
      id +
      "\", recipientId\"=\"" +
      recipientId +
      "\", widgetId\"=\"" +
      widgetId +
      "\", briefDescription\"=\"" +
      briefDescription +
      "\", fullDescription\"=\"" +
      fullDescription +
      "\", accumulatedAmount\"=\"" +
      accumulatedAmount +
      "\", requiredAmount\"=\"" +
      requiredAmount +
      "}"
    );
  }
}

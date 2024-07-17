package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.commons.Amount;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class GoalCommand {
  private String type;
  private String goalId;
  private String fullDescription;
  private String briefDescription;
  private Amount requiredAmount;
  private Amount accumulatedAmount;

  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getGoalId() {
    return goalId;
  }
  public void setGoalId(String goalId) {
    this.goalId = goalId;
  }
  public String getFullDescription() {
    return fullDescription;
  }
  public void setFullDescription(String fullDescription) {
    this.fullDescription = fullDescription;
  }
  public String getBriefDescription() {
    return briefDescription;
  }
  public void setBriefDescription(String briefDescription) {
    this.briefDescription = briefDescription;
  }
  public Amount getRequiredAmount() {
    return requiredAmount;
  }
  public void setRequiredAmount(Amount requiredAmount) {
    this.requiredAmount = requiredAmount;
  }
  public Amount getAccumulatedAmount() {
    return accumulatedAmount;
  }
  public void setAccumulatedAmount(Amount accumulatedAmount) {
    this.accumulatedAmount = accumulatedAmount;
  }

}

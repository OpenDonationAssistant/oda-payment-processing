package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import io.micronaut.data.annotation.Transient;
import io.micronaut.serde.ObjectMapper;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Goal extends GoalData {

  private final Logger log = LoggerFactory.getLogger(Goal.class);

  @Transient
  private final GoalCommandSender commandSender;

  @Transient
  private final GoalRepository repository;

  public Goal(
    String widgetId,
    String recipientId,
    String goalId,
    Boolean isDefault,
    GoalCommandSender commandSender,
    GoalRepository repository
  ) {
    this.setWidgetId(widgetId);
    this.setRecipientId(recipientId);
    this.commandSender = commandSender;
    this.repository = repository;
    this.setId(goalId);
    this.setRequiredAmount(new Amount(0, 0, "RUB"));
    this.setAccumulatedAmount(new Amount(0, 0, "RUB"));
    this.setDefault(isDefault);
  }

  public Goal handlePayment(CompletedPaymentNotification payment) {
    var paid = payment.getAmount().getMajor();
    var oldAmount = this.getAccumulatedAmount();
    this.setAccumulatedAmount(
        new Amount(
          oldAmount.getMajor() + paid,
          oldAmount.getMinor(),
          oldAmount.getCurrency()
        )
      );
    log.debug("Updating goal {} to {}", getId(), this);
    repository.update(this);
    return this;
  }

  public Goal update(Map<String, Object> config) {
    var fullDescription = (String) config.get("fullDescription");
    var briefDescription = (String) config.get("briefDescription");
    var amount = (Integer) ((Map<String, Object>) config.get(
        "requiredAmount"
      )).get("major");
    var accumulatedAmount = (Integer) ((Map<String, Object>) config.getOrDefault(
        "accumulatedAmount", Map.of("major", 0)
      )).getOrDefault("major", 0);
    var isDefault = (Boolean) config.get("default");
    this.setRequiredAmount(new Amount(amount, 0, "RUB"));
    this.setAccumulatedAmount(new Amount(accumulatedAmount, 0, "RUB"));
    this.setFullDescription(fullDescription);
    this.setBriefDescription(briefDescription);
    this.setDefault(isDefault);
    log.info("Update goal {} to {}", getId(), this);
    repository.update(this);
    return this;
  }

  public void delete(){
    repository.delete(this);
  }

  public UpdatedGoal asUpdatedGoal(){
    var updatedGoal = new UpdatedGoal();
    updatedGoal.setAccumulatedAmount(this.getAccumulatedAmount());
    updatedGoal.setWidgetId(this.getWidgetId());
    updatedGoal.setRequiredAmount(this.getRequiredAmount());
    updatedGoal.setBriefDescription(this.getBriefDescription());
    updatedGoal.setFullDescription(this.getFullDescription());
    updatedGoal.setGoalId(this.getId());
    updatedGoal.setRecipientId(this.getRecipientId());
    updatedGoal.setIsDefault(this.isDefault());
    return updatedGoal;
  }

  public GoalCommand createUpdateCommand() {
    var command = new GoalCommand();
    command.setAccumulatedAmount(this.getAccumulatedAmount());
    command.setRequiredAmount(this.getRequiredAmount());
    command.setBriefDescription(this.getBriefDescription());
    command.setFullDescription(this.getFullDescription());
    command.setGoalId(this.getId());
    command.setType("update");
    return command;
  }

  public Map<String, Object> asWidgetConfigGoal() {
      return Map.of(
        "id", this.getId(),
        "briefDescription", this.getBriefDescription(),
        "fullDescription", this.getFullDescription(),
        "accumulatedAmount", this.getAccumulatedAmount(),
        "requiredAmount", this.getRequiredAmount(),
        "default", this.isDefault()
      );
  }

  @Override
  public String toString() {
    try {
      return ObjectMapper.getDefault().writeValueAsString(this);
    } catch (Exception e) {
      return "Can't serialize Goal: " + e.getMessage();
    }
  }
}

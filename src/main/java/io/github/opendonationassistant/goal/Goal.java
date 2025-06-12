package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.events.goal.UpdatedGoal;
import io.github.opendonationassistant.events.widget.Widget;
import io.micronaut.data.annotation.Transient;
import java.util.Map;
import java.util.Optional;

public class Goal extends GoalData {

  private final ODALogger log = new ODALogger(this);

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
    var paid = payment.amount().getMajor();
    var oldAmount = this.getAccumulatedAmount();
    this.setAccumulatedAmount(
        new Amount(
          oldAmount.getMajor() + paid,
          oldAmount.getMinor(),
          oldAmount.getCurrency()
        )
      );
    return update();
  }

  public Goal update(Boolean enabled, Map<String, Object> config) {
    var fullDescription = (String) config.getOrDefault("fullDescription", "");
    var briefDescription = (String) config.getOrDefault("briefDescription", "");
    var amount = Optional.ofNullable((Map<String, Object>) config.get("requiredAmount"))
      .map(it -> (Integer)it.get("major"))
      .orElse(0);
    var accumulatedAmount = Optional.ofNullable(
      (Map<String, Object>) config.get("accumulatedAmount")
    )
      .map(it -> (Integer)it.get("major"))
      .orElse(0);

    var isDefault = (Boolean) config.getOrDefault("default", false);
    this.setRequiredAmount(new Amount(amount, 0, "RUB"));
    this.setAccumulatedAmount(new Amount(accumulatedAmount, 0, "RUB"));
    this.setFullDescription(fullDescription);
    this.setBriefDescription(briefDescription);
    this.setDefault(isDefault);
    this.setEnabled(enabled);
    return update();
  }

  private Goal update() {
    log.info("Updating goal", Map.of("id", getId(), "goal", this));
    repository.update(this);
    return this;
  }

  public void delete() {
    log.info("Deleting goal", Map.of("goal", this));
    repository.delete(this);
  }

  public UpdatedGoal asUpdatedGoal() {
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
      "id",
      this.getId(),
      "briefDescription",
      this.getBriefDescription(),
      "fullDescription",
      this.getFullDescription(),
      "accumulatedAmount",
      this.getAccumulatedAmount(),
      "requiredAmount",
      this.getRequiredAmount(),
      "default",
      this.isDefault()
    );
  }

  @Override
  public String toString() {
    return ToString.asJson(this);
  }
}

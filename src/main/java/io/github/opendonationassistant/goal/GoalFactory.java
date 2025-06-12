package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.commons.logging.ODALogger;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GoalFactory {

  private ODALogger log = new ODALogger(this);

  private final GoalRepository repository;
  private final GoalCommandSender commandSender;

  @Inject
  public GoalFactory(
    GoalRepository repository,
    GoalCommandSender commandSender
  ) {
    this.repository = repository;
    this.commandSender = commandSender;
  }

  public Goal getBy(String recipientId, String widgetId, String goalId) {
    return repository
      .getById(goalId)
      .map(this::from)
      .orElseGet(() -> {
        var newGoal = new Goal(
          widgetId,
          recipientId,
          goalId,
          false,
          true,
          commandSender,
          repository
        );
        log.info("Created goal", Map.of("goal", newGoal));
        repository.save(newGoal);
        return newGoal;
      });
  }

  public Optional<Goal> getBy(String goalId) {
    return repository.getById(goalId).map(this::from);
  }

  public List<Goal> findFor(String recipientId) {
    return repository
      .getByRecipientId(recipientId)
      .stream()
      .map(this::from)
      .toList();
  }

  public List<Goal> findFor(String recipientId, String widgetId) {
    return repository
      .getByRecipientIdAndWidgetId(recipientId, widgetId)
      .stream()
      .map(this::from)
      .toList();
  }

  private Goal from(GoalData data) {
    var goal = new Goal(
      data.getWidgetId(),
      data.getRecipientId(),
      data.getId(),
      data.isDefault(),
      data.getEnabled(),
      commandSender,
      repository
    );
    goal.setBriefDescription(data.getBriefDescription());
    goal.setFullDescription(data.getFullDescription());
    goal.setRequiredAmount(data.getRequiredAmount());
    goal.setAccumulatedAmount(data.getAccumulatedAmount());
    return goal;
  }
}

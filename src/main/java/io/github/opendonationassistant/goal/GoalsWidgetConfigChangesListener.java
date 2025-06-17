package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.config.ConfigCommandSender;
import io.github.opendonationassistant.config.ConfigPutCommand;
import io.github.opendonationassistant.events.goal.GoalSender;
import io.github.opendonationassistant.events.widget.Widget;
import io.github.opendonationassistant.events.widget.WidgetChangedEvent;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class GoalsWidgetConfigChangesListener {

  private static final String WIDGET_TYPE = "donationgoal";

  private final ODALogger log = new ODALogger(this);
  private final GoalFactory goalFactory;
  private final ConfigCommandSender configCommandSender;

  @Inject
  public GoalsWidgetConfigChangesListener(
    GoalFactory goalFactory,
    ConfigCommandSender configCommandSender
  ) {
    this.goalFactory = goalFactory;
    this.configCommandSender = configCommandSender;
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Configs.GOAL)
  public void listen(WidgetChangedEvent event) {
    if (event == null) {
      return;
    }
    log.info("Received goals configuration", Map.of("event", event));
    Widget widget = event.widget();
    if (widget == null) {
      return;
    }
    if (!WIDGET_TYPE.equals(widget.type())) {
      return;
    }

    if ("deleted".equals(event.type())) {
      goalFactory
        .findFor(widget.ownerId())
        .stream()
        .filter(goal -> widget.id().equals(goal.getWidgetId()))
        .forEach(goal -> {
          goal.delete();
        });
    }

    List<Goal> savedGoals = goalFactory.findFor(widget.ownerId());

    if ("updated".equals(event.type()) || "toggled".equals(event.type())) {
      List<Goal> updatedGoals = new ArrayList<>();
      widget
        .config()
        .properties()
        .stream()
        .forEach(property -> {
          if ("goal".equals(property.name())) {
            var goals = (List<Map<String, Object>>) property.value();
            updatedGoals.addAll(
              Optional.ofNullable(goals)
                .orElse(List.of())
                .stream()
                .map(config -> {
                  var id = (String) config.get("id");
                  return goalFactory
                    .getBy(widget.ownerId(), widget.id(), id)
                    .update(widget.enabled(), config);
                })
                .toList()
            );
          }
        });
      savedGoals
        .stream()
        .filter(goal -> widget.id().equals(goal.getWidgetId()))
        .filter(goal ->
          updatedGoals
            .stream()
            .filter(updated -> updated.getId().equals(goal.getId()))
            .findFirst()
            .isEmpty()
        )
        .forEach(Goal::delete);
    }

    savedGoals = goalFactory
      .findFor(widget.ownerId())
      .stream()
      .filter(goal -> goal.getEnabled())
      .toList();

    var command = new ConfigPutCommand();
    command.setKey("goals");
    command.setValue(savedGoals);
    command.setOwnerId(widget.ownerId());
    command.setName("paymentpage");
    configCommandSender.send(command);
  }
}

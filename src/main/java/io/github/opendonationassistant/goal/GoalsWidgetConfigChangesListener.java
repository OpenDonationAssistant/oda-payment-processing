package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.config.ConfigCommandSender;
import io.github.opendonationassistant.config.ConfigPutCommand;
import io.github.opendonationassistant.reel.Widget;
import io.github.opendonationassistant.reel.WidgetChangedEvent;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class GoalsWidgetConfigChangesListener {

  private static final String WIDGET_TYPE = "donationgoal";

  private final Logger log = LoggerFactory.getLogger(
    GoalsWidgetConfigChangesListener.class
  );
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
    log.info("Received goals configuration: {}", event);
    if (event == null) {
      return;
    }
    Widget widget = event.getWidget();
    if (widget == null) {
      return;
    }
    if (!WIDGET_TYPE.equals(widget.getType())) {
      return;
    }
    if ("deleted".equals(event.getType())) {
      goalFactory
        .findFor(widget.getOwnerId())
        .stream()
        .filter(goal -> widget.getId().equals(goal.getWidgetId()))
        .forEach(Goal::delete);
    }
    List<Goal> savedGoals = goalFactory.findFor(widget.getOwnerId());
    if (!"deleted".equals(event.getType())) {
      List<Goal> updatedGoals = new ArrayList<>();
      widget
        .getConfig()
        .getProperties()
        .stream()
        .forEach(property -> {
          if ("goal".equals(property.getName())) {
            var goals = (List<Map<String, Object>>) property.getValue();
            updatedGoals.addAll(
              goals
                .stream()
                .map(config -> {
                  var id = (String) config.get("id");
                  return goalFactory
                    .getBy(widget.getOwnerId(), widget.getId(), id)
                    .update(config);
                })
                .toList()
            );
          }
        });
      savedGoals
        .stream()
        .filter(goal -> widget.getId().equals(goal.getWidgetId()))
        .filter(goal ->
          updatedGoals
            .stream()
            .filter(updated -> updated.getId().equals(goal.getId()))
            .findFirst()
            .isEmpty()
        )
        .forEach(Goal::delete);
    }
    savedGoals = goalFactory.findFor(widget.getOwnerId());
    var command = new ConfigPutCommand();
    command.setKey("goals");
    command.setValue(savedGoals);
    command.setOwnerId(widget.getOwnerId());
    command.setName("paymentpage");
    configCommandSender.send(command);
  }
}

package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.config.ConfigCommandSender;
import io.github.opendonationassistant.config.ConfigPutCommand;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.events.goal.GoalSender;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.github.opendonationassistant.events.widget.WidgetProperty;
import io.github.opendonationassistant.events.widget.WidgetUpdateCommand;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class GoalsPaymentListener {

  private final Logger log = LoggerFactory.getLogger(
    GoalsPaymentListener.class
  );

  private final GoalFactory goalFactory;
  private final ConfigCommandSender configCommandSender;
  private final GoalCommandSender goalCommandSender;
  private final GoalSender goalSender;
  private final WidgetCommandSender widgetCommandSender;

  @Inject
  public GoalsPaymentListener(
    GoalFactory goalFactory,
    ConfigCommandSender configCommandSender,
    GoalCommandSender goalCommandSender,
    WidgetCommandSender widgetCommandSender,
    GoalSender goalSender
  ) {
    this.goalFactory = goalFactory;
    this.configCommandSender = configCommandSender;
    this.goalCommandSender = goalCommandSender;
    this.widgetCommandSender = widgetCommandSender;
    this.goalSender = goalSender;
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Payments.GOAL)
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification for goal: {}", payment);

    Optional<Goal> oldGoal = Optional.ofNullable(payment.getGoal()).flatMap(
      goalFactory::getBy
    );
    // TODO: optimize
    Optional<Goal> updatedGoal = oldGoal.map(goal -> goal.handlePayment(payment)
    );

    // TODO: make one flow
    List<Goal> savedGoals = goalFactory.findFor(payment.getRecipientId());
    var command = new ConfigPutCommand();
    command.setKey("goals");
    command.setValue(savedGoals);
    command.setOwnerId(payment.getRecipientId());
    command.setName("paymentpage");
    configCommandSender.send(command);

    oldGoal
      .map(goal -> goal.getWidgetId())
      .ifPresent(widgetId -> {
        List<Goal> goalList = goalFactory.findFor(
          payment.getRecipientId(),
          widgetId
        );
        var goals = new WidgetProperty();
        goals.setName("goal");
        goals.setValue(
          goalList
            .stream()
            .map(Goal::asWidgetConfigGoal)
            .reduce(
              new ArrayList<>(),
              (list, goal) -> {
                list.add(goal);
                return list;
              },
              (first, second) -> {
                first.addAll(second);
                return first;
              }
            )
        );
        var patch = new WidgetConfig();
        patch.setProperties(List.of(goals));
        widgetCommandSender.send(new WidgetUpdateCommand(widgetId, patch));
      });

    // TODO: send 1 message instead of 3 ( maybe use WidgetChangedNotification)
    // TODO: reload would be done without it, is it needed?
    updatedGoal
      .map(goal -> goal.createUpdateCommand())
      .ifPresent(updateCommand ->
        goalCommandSender.send(
          "%sgoal".formatted(payment.getRecipientId()),
          updateCommand
        )
      );

    updatedGoal.map(Goal::asUpdatedGoal).ifPresent(goalSender::sendUpdatedGoal);
  }
}

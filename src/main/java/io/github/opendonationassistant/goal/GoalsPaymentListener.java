package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.config.ConfigCommandSender;
import io.github.opendonationassistant.config.ConfigPutCommand;
import io.github.opendonationassistant.reel.CompletedPaymentNotification;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
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

  @Inject
  public GoalsPaymentListener(
    GoalFactory goalFactory,
    ConfigCommandSender configCommandSender,
    GoalCommandSender goalCommandSender
  ) {
    this.goalFactory = goalFactory;
    this.configCommandSender = configCommandSender;
    this.goalCommandSender = goalCommandSender;
  }

  @Queue("payments_for_goal")
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification: {}", payment);
    Optional<Goal> updatedGoal = Optional
      .ofNullable(payment.getGoal())
      .flatMap(goalFactory::getBy)
      .map(goal -> goal.handlePayment(payment));
    List<Goal> savedGoals = goalFactory.findFor(payment.getRecipientId());
    var command = new ConfigPutCommand();
    command.setKey("goals");
    command.setValue(savedGoals);
    command.setOwnerId(payment.getRecipientId());
    command.setName("paymentpage");
    configCommandSender.send(command);
    updatedGoal.map(goal  -> goal.createUpdateCommand())
      .ifPresent(updateCommand -> goalCommandSender.send("%sgoal".formatted(payment.getRecipientId()), updateCommand));
  }
}

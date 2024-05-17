package io.github.opendonationassistant.goal;

import io.github.opendonationassistant.config.ConfigCommandSender;
import io.github.opendonationassistant.config.ConfigPutCommand;
import io.github.opendonationassistant.reel.CompletedPaymentNotification;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class GoalsPaymentListener {

  private final Logger log = LoggerFactory.getLogger(GoalsPaymentListener.class);

  private final GoalFactory goalFactory;
  private final ConfigCommandSender configCommandSender;

  @Inject
  public GoalsPaymentListener(GoalFactory goalFactory, ConfigCommandSender configCommandSender) {
    this.goalFactory = goalFactory;
    this.configCommandSender = configCommandSender;
  }

  @Queue("payments_for_goal")
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification: {}", payment);
    goalFactory
      .getBy(payment.getGoal())
      .ifPresent(goal -> goal.handlePayment(payment));
    List<Goal> savedGoals = goalFactory.findFor(payment.getRecipientId());
    var command = new ConfigPutCommand();
    command.setKey("goals");
    command.setValue(savedGoals);
    command.setOwnerId(payment.getRecipientId());
    command.setName("paymentpage");
    configCommandSender.send(command);
  }

}

package io.github.opendonationassistant.history;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.goal.GoalFactory;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class HistoryPaymentListener {

  private Logger log = LoggerFactory.getLogger(HistoryPaymentListener.class);

  private final HistoryCommandSender commandSender;
  private final GoalFactory goalFactory;

  public HistoryPaymentListener(
    HistoryCommandSender commandSender,
    GoalFactory goalFactory
  ) {
    this.commandSender = commandSender;
    this.goalFactory = goalFactory;
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Payments.HISTORY)
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification: {}", payment);

    var partial = new HistoryItemData();
    partial.setId(Generators.timeBasedEpochGenerator().generate().toString());
    partial.setAmount(payment.amount());
    partial.setMessage(payment.message());
    partial.setNickname(payment.nickname());
    partial.setPaymentId(payment.id());
    partial.setRecipientId(payment.recipientId());
    partial.setAuthorizationTimestamp(payment.authorizationTimestamp());

    Optional.ofNullable(payment.attachments())
      .orElse(List.of())
      .stream()
      .map(attachmentId -> {
        var attachment = new Attachment();
        attachment.setId(attachmentId);
        return attachment;
      });

    Optional.ofNullable(payment.goal())
      .flatMap(goalFactory::getBy)
      .ifPresent(goal -> {
        var targetGoal = new TargetGoal();
        targetGoal.setGoalId(payment.goal());
        targetGoal.setGoalTitle(goal.getBriefDescription());
        partial.setGoals(List.of(targetGoal));
      });

    var command = new HistoryCommand();
    command.setType("update");
    command.setPartial(partial);
    commandSender.send("history", command);
  }
}

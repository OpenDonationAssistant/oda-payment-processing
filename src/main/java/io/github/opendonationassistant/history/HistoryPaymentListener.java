package io.github.opendonationassistant.history;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.goal.GoalFactory;
import io.github.opendonationassistant.reel.CompletedPaymentNotification;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import java.util.List;
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

  @Queue("payments_for_history")
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification: {}", payment);

    var partial = new HistoryItemData();
    partial.setId(Generators.timeBasedEpochGenerator().generate().toString());
    partial.setAmount(payment.getAmount());
    partial.setMessage(payment.getMessage());
    partial.setNickname(payment.getNickname());
    partial.setPaymentId(payment.getId());
    partial.setRecipientId(payment.getRecipientId());
    partial.setAuthorizationTimestamp(payment.getAuthorizationTimestamp());

    payment.getAttachments().stream().map(attachmentId -> {
      var attachment = new Attachment();
      attachment.setId(attachmentId);
      return attachment;
    });

    goalFactory
      .getBy(payment.getGoal())
      .ifPresent(goal -> {
        var targetGoal = new TargetGoal();
        targetGoal.setGoalId(payment.getGoal());
        targetGoal.setGoalTitle(goal.getBriefDescription());
        partial.setGoals(List.of(targetGoal));
      });

    var command = new HistoryCommand();
    command.setType("update");
    command.setPartial(partial);
    commandSender.send("history", command);
  }
}

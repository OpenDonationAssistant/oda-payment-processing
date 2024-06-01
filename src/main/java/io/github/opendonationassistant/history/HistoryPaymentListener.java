package io.github.opendonationassistant.history;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.uuid.Generators;

import io.github.opendonationassistant.reel.CompletedPaymentNotification;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;

@RabbitListener
public class HistoryPaymentListener {
  private Logger log = LoggerFactory.getLogger(HistoryPaymentListener.class);

  private final HistoryCommandSender commandSender;

  public HistoryPaymentListener(HistoryCommandSender commandSender){
    this.commandSender = commandSender;
  }

  @Queue("payments_for_history")
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification: {}", payment);
    var partial = new HistoryItemData();
    partial.setId(Generators.timeBasedEpochGenerator().toString());
    partial.setAmount(payment.getAmount());
    partial.setMessage(payment.getMessage());
    partial.setNickname(payment.getNickname());
    partial.setPaymentId(payment.getId());
    partial.setRecipientId(payment.getRecipientId());
    partial.setAuthorizationTimestamp(payment.getAuthorizationTimestamp());

    var command = new HistoryCommand();
    command.setType("update");
    command.setPartial(partial);
    commandSender.send("history", command);
  }
}

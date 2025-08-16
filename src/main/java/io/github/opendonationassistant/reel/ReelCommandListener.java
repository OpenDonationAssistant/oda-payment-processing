package io.github.opendonationassistant.reel;

import io.github.opendonationassistant.events.history.HistoryCommand;
import io.github.opendonationassistant.events.history.HistoryCommandSender;
import io.github.opendonationassistant.events.history.HistoryItemData;
import io.github.opendonationassistant.events.history.ReelResult;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.List;

@RabbitListener
public class ReelCommandListener {

  private final ReelWidgetCommandSender commandSender;
  private final HistoryCommandSender historyCommandSender;

  @Inject
  public ReelCommandListener(
    ReelWidgetCommandSender commandSender,
    HistoryCommandSender historyCommandSender
  ) {
    this.commandSender = commandSender;
    this.historyCommandSender = historyCommandSender;
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Commands.REEL)
  public void listen(ReelCommand command) {
    HistoryItemData data = new HistoryItemData(
      null,
      command.getPaymentId(),
      null,
      null,
      command.getRecipientId(),
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      List.of(new ReelResult(command.getSelection()))
    );
    historyCommandSender.send(
      "history",
      new HistoryCommand("update", data, false, false, false, false, false)
    );
    commandSender.send("%sreel".formatted(command.getRecipientId()), command);
  }
}

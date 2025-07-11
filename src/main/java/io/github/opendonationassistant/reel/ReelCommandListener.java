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
    HistoryCommand historyCommand = new HistoryCommand();
    HistoryItemData data = new HistoryItemData();
    data.setPaymentId(command.getPaymentId());
    ReelResult result = new ReelResult();
    result.setTitle(command.getSelection());
    data.setReelResults(List.of(result));
    historyCommand.setPartial(data);
    historyCommand.setType("update");
    historyCommandSender.send("history", historyCommand);
    commandSender.send("%sreel".formatted(command.getRecipientId()), command);
  }
}

package io.github.opendonationassistant.history;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;

@RabbitClient("commands")
public interface HistoryCommandSender {
  void send(@Binding String binding, HistoryCommand command);
}

package io.github.opendonationassistant.reel;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;

@RabbitClient("amq.topic")
public interface ReelCommandSender {
  void send(@Binding String binding, ReelCommand command);
}


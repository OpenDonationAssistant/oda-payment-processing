package io.github.opendonationassistant.goal;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;

@RabbitClient("amq.topic")
public interface GoalCommandSender {
  void send(@Binding String binding, GoalCommand command);
}

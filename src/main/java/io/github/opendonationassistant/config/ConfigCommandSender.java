package io.github.opendonationassistant.config;

import io.micronaut.rabbitmq.annotation.Binding;
import io.micronaut.rabbitmq.annotation.RabbitClient;

@RabbitClient("commands")
public interface ConfigCommandSender {
  void send(@Binding String binding, ConfigPutCommand command);

  default void send(ConfigPutCommand command) {
    send("config", command);
  }
}

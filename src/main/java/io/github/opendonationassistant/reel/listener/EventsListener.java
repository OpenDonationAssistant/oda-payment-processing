package io.github.opendonationassistant.reel.listener;

import io.github.opendonationassistant.events.MessageProcessor;
import io.micronaut.messaging.annotation.MessageHeader;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import io.micronaut.rabbitmq.bind.RabbitAcknowledgement;
import jakarta.inject.Inject;

@RabbitListener
public class EventsListener {

  private MessageProcessor processor;

  @Inject
  public EventsListener(MessageProcessor processor) {
    this.processor = processor;
  }

  @Queue("payments_for_reel")
  public void listen(
    @MessageHeader String type,
    byte[] payment,
    RabbitAcknowledgement ack
  ) {
    processor.process(type, payment, ack);
  }
}

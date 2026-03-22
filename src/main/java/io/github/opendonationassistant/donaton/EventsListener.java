package io.github.opendonationassistant.donaton;

import io.github.opendonationassistant.donaton.handlers.HistoryItemEventHandler;
import io.github.opendonationassistant.donaton.handlers.PaymentEventHandler;
import io.github.opendonationassistant.events.MessageProcessor;
import io.micronaut.messaging.annotation.MessageHeader;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import io.micronaut.rabbitmq.bind.RabbitAcknowledgement;
import jakarta.inject.Inject;
import java.util.List;

@RabbitListener
public class EventsListener {

  private final MessageProcessor processor;

  @Inject
  public EventsListener(
    PaymentEventHandler paymentHandler,
    HistoryItemEventHandler historyHandler
  ) {
    this.processor = new MessageProcessor(
      List.of(paymentHandler, historyHandler)
    );
  }

  @Queue(io.github.opendonationassistant.rabbit.Queue.Donaton.EVENTS)
  public void listen(
    @MessageHeader String type,
    byte[] event,
    RabbitAcknowledgement ack
  ) {
    processor.process(type, event, ack);
  }
}

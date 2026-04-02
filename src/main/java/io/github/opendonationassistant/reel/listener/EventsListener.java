package io.github.opendonationassistant.reel.listener;

import io.github.opendonationassistant.events.MessageProcessor;
import io.github.opendonationassistant.reel.listener.handler.HistoryEventHandler;
import io.github.opendonationassistant.reel.listener.handler.LinkReelCommandHandler;
import io.github.opendonationassistant.reel.listener.handler.PaymentEventHandler;
import io.github.opendonationassistant.reel.listener.handler.TriggerReelHandler;
import io.micronaut.messaging.annotation.MessageHeader;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import io.micronaut.rabbitmq.bind.RabbitAcknowledgement;
import jakarta.inject.Inject;
import java.util.List;

@RabbitListener
public class EventsListener {

  private MessageProcessor processor;

  @Inject
  public EventsListener(
    HistoryEventHandler historyHandler,
    PaymentEventHandler paymentHandler,
    TriggerReelHandler triggerHandler,
    LinkReelCommandHandler linkReelCommandHandler
  ) {
    this.processor = new MessageProcessor(
      List.of(
        historyHandler,
        paymentHandler,
        triggerHandler,
        linkReelCommandHandler
      )
    );
  }

  @Queue("reel.events")
  public void listen(
    @MessageHeader String type,
    byte[] payment,
    RabbitAcknowledgement ack
  ) {
    processor.process(type, payment, ack);
  }
}

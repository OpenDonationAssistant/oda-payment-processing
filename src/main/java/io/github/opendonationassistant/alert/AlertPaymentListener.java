package io.github.opendonationassistant.alert;

import static io.github.opendonationassistant.rabbit.Queue.Payments.ALERTS;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.alerts.AlertNotification;
import io.github.opendonationassistant.events.alerts.AlertSender;
import io.github.opendonationassistant.events.history.event.HistoryItemEvent;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@RabbitListener
public class AlertPaymentListener {

  private final ODALogger log = new ODALogger(this);
  private final AlertSender sender;

  @Inject
  public AlertPaymentListener(AlertSender sender) {
    this.sender = sender;
  }

  @Queue(ALERTS)
  public void listen(HistoryItemEvent payment) {
    log.info("Received notification", Map.of("payment", payment));
    sender.send(
      payment.recipientId(),
      new AlertNotification(
        payment.id(),
        payment.type(),
        payment.nickname(),
        payment.message(),
        payment.recipientId(),
        payment.amount(),
        List.of(), // attachments
        null, // goal
        List.of(), // actions
        payment.timestamp(),
        null, // alertmedia
        payment.system()
      )
    );
  }
}

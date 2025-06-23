package io.github.opendonationassistant.reel;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;

import java.util.Map;

@RabbitListener
public class ReelPaymentListener {

  private final ODALogger log = new ODALogger(this);
  private final ReelFactory reelFactory;

  @Inject
  public ReelPaymentListener(ReelFactory reelFactory) {
    this.reelFactory = reelFactory;
  }

  @Queue("payments_for_reel")
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification", Map.of("payment", payment));
    reelFactory
      .findFor(payment.recipientId())
      .forEach(reel -> reel.handlePayment(payment));
  }
}

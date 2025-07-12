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
  private final ReelRepository reels;

  @Inject
  public ReelPaymentListener(ReelRepository reels) {
    this.reels = reels;
  }

  @Queue("payments_for_reel")
  public void listen(CompletedPaymentNotification payment) {
    log.debug("Received notification", Map.of("payment", payment));
    reels
      .findFor(payment.recipientId())
      .forEach(reel -> reel.handlePayment(payment));
  }
}

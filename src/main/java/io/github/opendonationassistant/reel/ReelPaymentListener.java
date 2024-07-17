package io.github.opendonationassistant.reel;

import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class ReelPaymentListener {
  private Logger log = LoggerFactory.getLogger(ReelPaymentListener.class);

  private final ReelFactory reelFactory;

  @Inject
  public ReelPaymentListener(ReelFactory reelFactory) {
    this.reelFactory = reelFactory;
  }

  @Queue("payments_for_reel")
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification: {}", payment);
    reelFactory
      .findFor(payment.getRecipientId())
      .forEach(reel -> reel.handlePayment(payment));
  }
}

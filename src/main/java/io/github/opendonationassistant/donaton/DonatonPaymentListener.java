package io.github.opendonationassistant.donaton;

import static io.github.opendonationassistant.rabbit.Queue.Payments.DONATON;

import io.github.opendonationassistant.donaton.repository.DonatonRepository;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class DonatonPaymentListener {

  private Logger log = LoggerFactory.getLogger(DonatonPaymentListener.class);

  private final DonatonRepository repository;

  @Inject
  public DonatonPaymentListener(DonatonRepository repository) {
    this.repository = repository;
  }

  @Queue(DONATON)
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification for donaton: {}", payment);
    repository
      .findFor(payment.recipientId())
      .forEach(donaton -> donaton.handlePayment(payment));
  }
}

package io.github.opendonationassistant.donaton;

import static io.github.opendonationassistant.rabbit.Queue.Payments.DONATON;

import java.util.Map;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.donaton.repository.DonatonRepository;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;

@RabbitListener
public class DonatonPaymentListener {

  private final ODALogger log = new ODALogger(this);
  private final DonatonRepository repository;

  @Inject
  public DonatonPaymentListener(DonatonRepository repository) {
    this.repository = repository;
  }

  @Queue(DONATON)
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification for donaton", Map.of("payment",payment));
    repository
      .findFor(payment.recipientId())
      .forEach(donaton -> donaton.handlePayment(payment));
  }
}

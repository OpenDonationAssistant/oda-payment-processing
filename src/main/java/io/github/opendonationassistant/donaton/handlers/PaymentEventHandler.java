package io.github.opendonationassistant.donaton.handlers;

import io.github.opendonationassistant.donaton.repository.DonatonRepository;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;

@Singleton
public class PaymentEventHandler extends AbstractMessageHandler<PaymentEvent> {

  private final DonatonRepository repository;

  @Inject
  public PaymentEventHandler(
    ObjectMapper mapper,
    DonatonRepository repository
  ) {
    super(mapper);
    this.repository = repository;
  }

  @Override
  public void handle(PaymentEvent payment) throws IOException {
    repository
      .findFor(payment.recipientId())
      .forEach(donaton -> donaton.handlePayment(payment));
  }
}

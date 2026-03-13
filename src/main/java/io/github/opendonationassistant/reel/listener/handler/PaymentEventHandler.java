package io.github.opendonationassistant.reel.listener.handler;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.MessageHandler;
import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.github.opendonationassistant.reel.repository.ReelRepository;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Map;

@Singleton
public class PaymentEventHandler extends AbstractMessageHandler<PaymentEvent> {

  private ODALogger log = new ODALogger(this);
  private final ReelRepository repository;

  @Inject
  public PaymentEventHandler(ReelRepository repository, ObjectMapper mapper) {
    super(mapper);
    this.repository = repository;
  }

  @Override
  public void handle(PaymentEvent event) throws IOException {
    log.debug("Received PaymentEvent", Map.of("event", event));
    repository
      .findFor(event.recipientId())
      .forEach(reel -> reel.handlePayment(event));
  }
}

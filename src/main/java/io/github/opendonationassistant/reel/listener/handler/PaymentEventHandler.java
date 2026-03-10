package io.github.opendonationassistant.reel.listener.handler;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.MessageHandler;
import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.github.opendonationassistant.reel.repository.ReelRepository;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Map;

@Singleton
public class PaymentEventHandler implements MessageHandler {

  private ODALogger log = new ODALogger(this);
  private final ReelRepository repository;
  private final ObjectMapper mapper;

  public PaymentEventHandler(ReelRepository repository, ObjectMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public void handle(byte[] message) throws IOException {
    var event = mapper.readValue(message, PaymentEvent.class);
    if (event == null) {
      return;
    }
    log.debug("Received PaymentEvent", Map.of("event", event));
    repository
      .findFor(event.recipientId())
      .forEach(reel -> reel.handlePayment(event));
  }

  @Override
  public String type() {
    return "PaymentEvent";
  }
}

package io.github.opendonationassistant.reel.listener.handler;

import io.github.opendonationassistant.events.MessageHandler;
import io.github.opendonationassistant.events.reel.ReelCommand.TriggerReelCommand;
import io.github.opendonationassistant.reel.repository.Reel;
import io.github.opendonationassistant.reel.repository.ReelRepository;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

@Singleton
public class TriggerReelHandler implements MessageHandler {

  private final ObjectMapper mapper;
  private final ReelRepository repository;

  @Inject
  public TriggerReelHandler(ObjectMapper mapper, ReelRepository repository) {
    this.mapper = mapper;
    this.repository = repository;
  }

  @Override
  public void handle(byte[] message) throws IOException {
    var event = mapper.readValue(message, TriggerReelCommand.class);
    Optional.ofNullable(event)
      .flatMap(it -> repository.getBy(it.recipientId(), it.widgetId()))
      .ifPresent(Reel::run);
  }

  @Override
  public String type() {
    return "TriggerReelCommand";
  }
}

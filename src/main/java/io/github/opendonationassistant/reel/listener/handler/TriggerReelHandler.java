package io.github.opendonationassistant.reel.listener.handler;

import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.reel.ReelCommand.TriggerReelCommand;
import io.github.opendonationassistant.reel.repository.ReelRepository;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

@Singleton
public class TriggerReelHandler
  extends AbstractMessageHandler<TriggerReelCommand> {

  private final ReelRepository repository;

  @Inject
  public TriggerReelHandler(ObjectMapper mapper, ReelRepository repository) {
    super(mapper);
    this.repository = repository;
  }

  @Override
  public void handle(TriggerReelCommand event) throws IOException {
    Optional.ofNullable(event)
      .flatMap(it -> repository.getBy(it.recipientId(), it.widgetId()))
      .ifPresent(reel -> reel.run(event.source(), event.originId()));
  }
}

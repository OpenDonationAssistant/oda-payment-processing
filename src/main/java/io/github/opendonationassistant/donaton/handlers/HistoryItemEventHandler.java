package io.github.opendonationassistant.donaton.handlers;

import io.github.opendonationassistant.donaton.repository.DonatonLinkRepository;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.donaton.DonatonFacade;
import io.github.opendonationassistant.events.donaton.events.DonatonDeadlineChanged;
import io.github.opendonationassistant.events.history.event.HistoryItemEvent;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

@Singleton
public class HistoryItemEventHandler
  extends AbstractMessageHandler<HistoryItemEvent> {

  private final DonatonLinkRepository repository;
  private final DonatonFacade facade;

  @Inject
  public HistoryItemEventHandler(
    ObjectMapper mapper,
    DonatonLinkRepository repository,
    DonatonFacade facade
  ) {
    super(mapper);
    this.repository = repository;
    this.facade = facade;
  }

  @Override
  public void handle(HistoryItemEvent message) throws IOException {
    Optional.ofNullable(message.originId())
      .flatMap(repository::findByOriginId)
      .ifPresent(change -> {
        facade.sendEvent(
          new DonatonDeadlineChanged(
            change.source(),
            change.originId(),
            change.donatonId(),
            change.before(),
            change.after()
          )
        );
      });
  }
}

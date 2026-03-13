package io.github.opendonationassistant.reel.listener.handler;

import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.events.history.event.HistoryItemEvent;
import io.github.opendonationassistant.events.reel.ReelCommand.TriggerReelCommand;
import io.github.opendonationassistant.events.reel.ReelFacade;
import io.github.opendonationassistant.reel.repository.ReelLinkRepository;
import io.github.opendonationassistant.reel.repository.ReelRepository;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

@Singleton
public class HistoryEventHandler
  extends AbstractMessageHandler<HistoryItemEvent> {

  private final ReelLinkRepository reelLinkRepository;
  private final ReelRepository reelRepository;
  private final ReelFacade reelFacade;

  @Inject
  public HistoryEventHandler(
    ObjectMapper objectMapper,
    ReelLinkRepository reelLinkRepository,
    ReelRepository reelRepository,
    ReelFacade reelFacade
  ) {
    super(objectMapper);
    this.reelLinkRepository = reelLinkRepository;
    this.reelRepository = reelRepository;
    this.reelFacade = reelFacade;
  }

  @Override
  public void handle(HistoryItemEvent event) throws IOException {
    Optional.ofNullable(event.originId())
      .flatMap(reelLinkRepository::findByOriginId)
      .flatMap(link -> reelRepository.getById(link.reelId()))
      .ifPresent(reel ->
        reelFacade
          .sendCommand(
            new TriggerReelCommand(
              reel.data().widgetConfigId(),
              reel.data().recipientId(),
              "payment",
              event.originId()
            )
          )
          .join()
      );
  }
}

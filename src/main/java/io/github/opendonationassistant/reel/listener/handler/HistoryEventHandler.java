package io.github.opendonationassistant.reel.listener.handler;

import io.github.opendonationassistant.events.MessageHandler;
import io.github.opendonationassistant.events.history.event.HistoryItemEvent;
import io.github.opendonationassistant.events.reel.ReelCommand.TriggerReelCommand;
import io.github.opendonationassistant.reel.repository.ReelLinkRepository;
import io.github.opendonationassistant.reel.repository.ReelRepository;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

@Singleton
public class HistoryEventHandler implements MessageHandler {

  private final ObjectMapper objectMapper;
  private final ReelLinkRepository reelLinkRepository;
  private final ReelRepository reelRepository;

  @Inject
  public HistoryEventHandler(
    ObjectMapper objectMapper,
    ReelLinkRepository reelLinkRepository,
    ReelRepository reelRepository
  ) {
    this.objectMapper = objectMapper;
    this.reelLinkRepository = reelLinkRepository;
    this.reelRepository = reelRepository;
  }

  @Override
  public void handle(byte[] message) throws IOException {
    var event = objectMapper.readValue(message, HistoryItemEvent.class);
    Optional.ofNullable(event)
      .map(HistoryItemEvent::originId)
      .flatMap(reelLinkRepository::findByOriginId)
      .flatMap(link -> reelRepository.getById(link.reelId()))
      .ifPresent(reel ->
        new TriggerReelCommand(
          reel.data().widgetConfigId(),
          reel.data().recipientId()
        )
      );
  }

  @Override
  public String type() {
    return "HistoryItemEvent";
  }
}

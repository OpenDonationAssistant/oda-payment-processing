package io.github.opendonationassistant.reel.listener.handler;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.github.opendonationassistant.reel.repository.ReelRepository;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Singleton;
import java.io.IOException;

@Singleton
public class LinkReelCommandHandler
  extends AbstractMessageHandler<
    io.github.opendonationassistant.reel.listener.handler.LinkReelCommandHandler.LinkReelCommand
  > {

  private final ReelRepository repository;

  public LinkReelCommandHandler(
    ObjectMapper mapper,
    ReelRepository repository
  ) {
    super(mapper);
    this.repository = repository;
  }

  @Serdeable
  public static record LinkReelCommand(
    String recipientId,
    String paymentId,
    Amount amount
  ) {}

  @Override
  public void handle(LinkReelCommand message) throws IOException {
    repository
      .findFor(message.recipientId())
      .forEach(reel -> reel.handleTrigger(message.amount(), message.paymentId())
      );
  }
}

package io.github.opendonationassistant.donaton.handlers;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.donaton.repository.DonatonRepository;
import io.github.opendonationassistant.events.AbstractMessageHandler;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.inject.Singleton;

import java.io.IOException;

@Singleton
public class ChangeDonatonCommandHandler
  extends AbstractMessageHandler<
    ChangeDonatonCommandHandler.ChangeDonatonCommand
  > {

  private final DonatonRepository repository;

  public ChangeDonatonCommandHandler(
    ObjectMapper mapper,
    DonatonRepository repository
  ) {
    super(mapper);
    this.repository = repository;
  }

  @Serdeable
  public static record ChangeDonatonCommand(
    String recipientId,
    Amount change,
    String paymentId
  ) {}

  @Override
  public void handle(
    io.github.opendonationassistant.donaton.handlers.ChangeDonatonCommandHandler.ChangeDonatonCommand command
  ) throws IOException {
    repository
      .findFor(command.recipientId())
      .forEach(donaton ->
        donaton.handleChange(command.change(), command.paymentId())
      );
  }
}

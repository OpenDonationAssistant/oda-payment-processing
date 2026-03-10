package io.github.opendonationassistant.donaton.repository;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.donaton.Donaton;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class DonatonRepository {

  private final DonatonDataRepository repository;
  private final DonatonLinkRepository linkRepository;
  private final WidgetCommandSender commandSender;
  private final ODALogger log = new ODALogger(this);

  @Inject
  public DonatonRepository(
    DonatonDataRepository repository,
    WidgetCommandSender commandSender,
    DonatonLinkRepository linkRepository
  ) {
    this.repository = repository;
    this.commandSender = commandSender;
    this.linkRepository = linkRepository;
  }

  public List<Donaton> findFor(String recipientId) {
    return this.repository.getByRecipientIdOrderByEndDateDesc(recipientId)
      .stream()
      .map(data -> new Donaton(data, repository, linkRepository, commandSender))
      .toList();
  }

  public Donaton byId(String recipientId, String id) {
    return this.repository.findById(id)
      .map(data -> new Donaton(data, repository, linkRepository, commandSender))
      .orElseGet(() -> {
        var freshData = new DonatonData(
          id,
          recipientId,
          Instant.now(),
          Map.of(),
          true
        );
        log.info("Creating new donaton", Map.of("data", freshData));
        repository.save(freshData);
        return new Donaton(
          freshData,
          repository,
          linkRepository,
          commandSender
        );
      });
  }
}

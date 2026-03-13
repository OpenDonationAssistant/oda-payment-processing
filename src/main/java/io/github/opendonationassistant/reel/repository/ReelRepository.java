package io.github.opendonationassistant.reel.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.reel.ReelFacade;
import io.github.opendonationassistant.events.widget.Widget;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReelRepository {

  private final ODALogger log = new ODALogger(this);
  private final ReelDataRepository repository;
  private final ReelFacade facade;
  private final ReelLinkRepository linkRepository;

  @Inject
  public ReelRepository(
    ReelDataRepository repository,
    ReelFacade facade,
    ReelLinkRepository reelLinkRepository
  ) {
    this.repository = repository;
    this.facade = facade;
    this.linkRepository = reelLinkRepository;
  }

  public Optional<Reel> getBy(String recipientId, String widgetId) {
    return repository.getByWidgetConfigId(widgetId).map(this::from);
  }

  public void delete(String recipientId, String widgetId) {
    repository
      .getByWidgetConfigId(widgetId)
      .ifPresent(data -> {
        repository.delete(data);
        log.info("Reel deleted", Map.of("id", data.id()));
      });
  }

  public Reel create(ReelData data) {
    log.info("Reel created", Map.of("reel", data));
    // TODO use repository create
    repository.save(data);
    return from(data);
  }

  public Reel create(Widget widget) {
    var created = new ReelData(
      Generators.timeBasedEpochGenerator().generate().toString(),
      widget.ownerId(),
      widget.id(),
      new Amount(0, 0, "RUB"),
      new Amount(100, 0, "RUB"),
      new Amount(0, 0, "RUB"),
      List.of(),
      true,
      false
    );
    return create(created).update(widget);
  }

  public List<Reel> findFor(String recipientId) {
    return repository
      .getByRecipientId(recipientId)
      .stream()
      .map(this::from)
      .toList();
  }

  private Reel from(ReelData data) {
    log.debug("Found data", Map.of("data", data));
    return new Reel(data, facade, repository, linkRepository);
  }

  public Optional<Reel> getById(String id) {
    return repository.findById(id).map(this::from);
  }
}

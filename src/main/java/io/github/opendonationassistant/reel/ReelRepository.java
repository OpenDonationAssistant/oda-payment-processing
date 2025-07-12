package io.github.opendonationassistant.reel;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.widget.Widget;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReelRepository {

  private final ODALogger log = new ODALogger(this);
  private final ReelDataRepository repository;
  private final ReelCommandSender commandSender;

  @Inject
  public ReelRepository(
    ReelDataRepository repository,
    ReelCommandSender commandSender
  ) {
    this.repository = repository;
    this.commandSender = commandSender;
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

  public Reel create(String recipientId, Widget widget) {
    var created = new ReelData(
      Generators.timeBasedEpochGenerator().generate().toString(),
      recipientId,
      widget.id(),
      new Amount(0, 0, "RUB"),
      new Amount(0, 0, "RUB"),
      List.of(),
      true
    );
    log.info("Reel created", Map.of("reel", created));
    repository.save(created);
    return from(created).update(widget);
  }

  public List<Reel> findFor(String recipientId) {
    return repository
      .getByRecipientId(recipientId)
      .stream()
      .map(this::from)
      .toList();
  }

  private Reel from(ReelData data) {
    return new Reel(data, commandSender, repository);
  }
}

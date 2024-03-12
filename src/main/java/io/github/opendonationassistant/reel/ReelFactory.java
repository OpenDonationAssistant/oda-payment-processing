package io.github.opendonationassistant.reel;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;

public class ReelFactory {

  private Logger log = LoggerFactory.getLogger(ReelFactory.class);

  private final ReelRepository repository;
  private final ReelCommandSender commandSender;

  @Inject
  public ReelFactory(
    ReelRepository repository,
    ReelCommandSender commandSender
  ) {
    this.repository = repository;
    this.commandSender = commandSender;
  }

  public Reel getBy(String recipientId, String widgetId) {
    return repository
      .getByWidgetConfigId(widgetId)
      .map(this::from)
      .orElseGet(() -> {
        var newReel = new Reel(
          commandSender,
          repository,
          recipientId,
          widgetId
        );
        log.info("Create reel: {}", newReel);
        repository.save(newReel);
        return newReel;
      });
  }

  public List<Reel> findFor(String recipientId) {
    return repository
      .getByRecipientId(recipientId)
      .stream()
      .map(this::from)
      .toList();
  }

  private Reel from(ReelData data) {
    var reel = new Reel(commandSender, repository, data.getRecipientId(), data.getWidgetConfigId());
    reel.setId(data.getId());
    reel.setItems(data.getItems());
    reel.setCondition(data.getCondition());
    reel.setRequiredAmount(data.getRequiredAmount());
    reel.setAccumulatedAmount(data.getAccumulatedAmount());
    return reel;
  }
}

package io.github.opendonationassistant.reel.listener;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.widget.WidgetChangedEvent;
import io.github.opendonationassistant.reel.repository.ReelRepository;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;

@RabbitListener
public class ConfigListener {

  private static final String WIDGET_TYPE = "reel";
  private final ODALogger log = new ODALogger(this);

  private final ReelRepository reels;

  @Inject
  public ConfigListener(ReelRepository reels) {
    this.reels = reels;
  }

  @Queue("config.reel")
  public void listen(WidgetChangedEvent event) {
    log.debug("Received widget configuration", Map.of("event", event));
    Optional.ofNullable(event)
      .map(WidgetChangedEvent::widget)
      .filter(widget -> WIDGET_TYPE.equals(widget.type()))
      .ifPresent(widget -> {
        switch (event.type()) {
          case "created":
            reels.create(widget);
            break;
          case "updated":
            reels
              .getBy(widget.ownerId(), widget.id())
              .ifPresent(reel -> reel.update(widget));
            break;
          case "deleted":
            reels.delete(widget.ownerId(), widget.id());
            break;
          case "toggled":
            reels
              .getBy(widget.ownerId(), widget.id())
              .ifPresent(reel -> reel.toggle());
            break;
          default:
            return;
        }
      });
  }
}

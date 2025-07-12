package io.github.opendonationassistant.reel;

import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.widget.WidgetChangedEvent;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class ReelWidgetConfigChangesListener {

  private static final String WIDGET_TYPE = "reel";
  private final ODALogger log = new ODALogger(this);

  private final ReelRepository reels;

  @Inject
  public ReelWidgetConfigChangesListener(ReelRepository reels) {
    this.reels = reels;
  }

  @Queue("config.reel")
  public void listen(WidgetChangedEvent event) {
    log.debug("Received widget configuration", Map.of("event", event));
    if (event == null) {
      return;
    }
    var widget = event.widget();
    if (widget == null) {
      return;
    }
    if (!WIDGET_TYPE.equals(widget.type())) {
      return;
    }
    if ("created".equals(event.type())) {
      reels.create(widget);
    }
    if ("updated".equals(event.type())) {
      reels
        .getBy(widget.ownerId(), widget.id())
        .ifPresent(reel -> reel.update(widget));
    }
    if ("deleted".equals(event.type())) {
      reels.delete(widget.ownerId(), widget.id());
    }
  }
}

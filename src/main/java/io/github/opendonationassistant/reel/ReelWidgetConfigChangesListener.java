package io.github.opendonationassistant.reel;

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

  private final Logger log = LoggerFactory.getLogger(
    ReelWidgetConfigChangesListener.class
  );
  private final ReelFactory reelFactory;

  @Inject
  public ReelWidgetConfigChangesListener(ReelFactory reelFactory) {
    this.reelFactory = reelFactory;
  }

  @Queue("config.reel")
  public void listen(WidgetChangedEvent event) {
    log.info("Received widget configuration", Map.of("event", event));
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
    if (!"deleted".equals(event.type())) {
      reelFactory.getBy(widget.ownerId(), widget.id()).update(widget);
    }
  }
}

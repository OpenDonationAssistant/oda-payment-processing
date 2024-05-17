package io.github.opendonationassistant.reel;

import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
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
    log.info("Received widget configuration: {}", event);
    if (event == null) {
      return;
    }
    var widget = event.getWidget();
    if (widget == null) {
      return;
    }
    if (!WIDGET_TYPE.equals(widget.getType())) {
      return;
    }
    if (!"deleted".equals(event.getType())) {
      reelFactory
        .getBy(widget.getOwnerId(), widget.getId())
        .update(widget.getConfig());
    }
  }
}

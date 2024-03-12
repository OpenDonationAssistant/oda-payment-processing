package io.github.opendonationassistant.reel;

import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class ReelWidgetConfigChangesListener {

  private Logger log = LoggerFactory.getLogger(ReelWidgetConfigChangesListener.class);
  private static final String WIDGET_TYPE = "reel";
  private final ReelFactory reelFactory;

  @Inject
  public ReelWidgetConfigChangesListener(ReelFactory reelFactory) {
    this.reelFactory = reelFactory;
  }

  @Queue("config.reel")
  public void listen(Widget widget) {
    log.info("Received widget configuration: {}", widget);
    if (widget == null) {
      return;
    }
    if (!WIDGET_TYPE.equals(widget.getType())) {
      return;
    }
    reelFactory
      .getBy(widget.getOwnerId(), widget.getId())
      .update(widget.getConfig());
  }
}

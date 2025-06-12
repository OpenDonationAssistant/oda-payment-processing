package io.github.opendonationassistant.donaton;

import io.github.opendonationassistant.donaton.repository.DonatonRepository;
import io.github.opendonationassistant.events.widget.WidgetChangedEvent;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class DonatonConfigListener {

  private static final String WIDGET_TYPE = "donaton";

  private final Logger log = LoggerFactory.getLogger(
    DonatonConfigListener.class
  );
  private final DonatonRepository repository;

  @Inject
  public DonatonConfigListener(DonatonRepository repository) {
    this.repository = repository;
  }

  @Queue("config.donaton")
  public void listen(WidgetChangedEvent event) {
    log.info("Received donaton configuration: {}", event);
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

    if ("deleted".equals(event.type())) {
      repository
        .byId(widget.ownerId(), widget.id())
        .update(widget.config());
    }

    if (!"deleted".equals(event.type())) {
      repository
        .byId(widget.ownerId(), widget.id())
        .update(widget.config());
    }
  }
}

package io.github.opendonationassistant;

import io.github.opendonationassistant.rabbit.AMQPConfiguration;
import io.github.opendonationassistant.rabbit.Exchange;
import io.github.opendonationassistant.rabbit.Queue;
import io.github.opendonationassistant.rabbit.RabbitExceptionHandler;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.ApplicationContextConfigurer;
import io.micronaut.context.annotation.ContextConfigurer;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.rabbitmq.connect.ChannelInitializer;
import io.micronaut.rabbitmq.exception.DefaultRabbitListenerExceptionHandler;
import io.micronaut.runtime.Micronaut;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;

@Factory
public class Application {

  @ContextConfigurer
  public static class DefaultEnvironmentConfigurer
    implements ApplicationContextConfigurer {

    @Override
    public void configure(ApplicationContextBuilder builder) {
      builder.defaultEnvironments("standalone");
    }
  }

  public static void main(String[] args) {
    Micronaut.build(args).mainClass(Application.class).banner(false).start();
  }

  @Singleton
  public ChannelInitializer rabbitConfiguration() {
    var reelEvents = new Queue("reel.events");
    var donatonEvents = new Queue("donaton.events");
    return new AMQPConfiguration(
      List.of(
        Exchange.Exchange(
          "changes.widgets",
          Map.of("reel", new Queue("config.reel"))
        ),
        Exchange.Exchange("reel", Map.of("command", new Queue("reel.command"))),
        Exchange.Exchange(
          "history",
          Map.of("event.HistoryItemEvent", reelEvents)
        ),
        Exchange.Exchange(
          "history",
          Map.of("event.LinkReelCommand", reelEvents)
        ),
        Exchange.Exchange("payments", Map.of("event.PaymentEvent", reelEvents)),

        Exchange.Exchange(
          "changes.widgets",
          Map.of("donaton", new Queue("config.donaton"))
        ),
        Exchange.Exchange(
          "history",
          Map.of("event.HistoryItemEvent", donatonEvents)
        ),
        Exchange.Exchange(
          "history",
          Map.of("event.ChangeDonatonCommand", donatonEvents)
        ),
        Exchange.Exchange(
          "payments",
          Map.of("event.PaymentEvent", donatonEvents)
        )
      )
    );
  }

  @Replaces(DefaultRabbitListenerExceptionHandler.class)
  @Singleton
  public RabbitExceptionHandler rabbitExceptionHandler() {
    return new RabbitExceptionHandler();
  }
}

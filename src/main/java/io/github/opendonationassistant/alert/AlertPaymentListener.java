package io.github.opendonationassistant.alert;

import static io.github.opendonationassistant.rabbit.Queue.Payments.ALERTS;

import io.github.opendonationassistant.art.ArtClient;
import io.github.opendonationassistant.art.ArtGenerationRequest;
import io.github.opendonationassistant.art.OperationDescription;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.events.alerts.AlertNotification.AlertMedia;
import io.github.opendonationassistant.events.alerts.AlertSender;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.StringUtils;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

@RabbitListener
public class AlertPaymentListener {

  private final ODALogger log = new ODALogger(this);
  private final AlertSender sender;
  private final ArtClient artClient;
  private final String token;
  private final String modelUri;
  private final ArtGenerationRequest.Options options;

  @Inject
  public AlertPaymentListener(
    AlertSender sender,
    ArtClient artClient,
    @Value("${art.token}") String token,
    @Value("${model.uri}") String modelUri
  ) {
    this.sender = sender;
    this.artClient = artClient;
    var options = new ArtGenerationRequest.Options();
    options.setSeed(String.valueOf(RandomGenerator.getDefault().nextInt()));
    options.setMimeType("image/jpeg");
    this.options = options;
    this.token = token;
    this.modelUri = modelUri;
  }

  private void sendUsualNotification(CompletedPaymentNotification payment) {
    sender.send(payment.recipientId(), payment.asAlertNotification());
  }

  private void sendNotificationWithGeneratedArt(
    CompletedPaymentNotification payment
  ) {
    log.info("Creating art", Map.of("id", payment.id()));
    var artRequest = new ArtGenerationRequest();
    artRequest.setModelUri(this.modelUri);
    artRequest.setMessages(
      List.of(new ArtGenerationRequest.Message(payment.cleanMessage(), 1))
    );
    artRequest.setGenerationOptions(this.options);
    var done = false;
    OperationDescription requested = null;
    try {
      requested = artClient.generate(token, artRequest);
      log.info("Requested art", Map.of("id", requested.getId()));
      var counter = 0;
      while (!done && counter < 10) {
        try {
          Thread.sleep(10000);
          var operation = artClient.operations(token, requested.getId());
          done = operation.isDone();
          counter++;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    var notification = payment.asAlertNotification();
    if (done) {
      notification = notification.withMedia(
        new AlertMedia("/generated/%s".formatted(requested.getId()))
      );
    }
    log.info("Sent alert for payment", Map.of("notification", notification));
    sender.send(payment.recipientId(), notification);
  }

  @Queue(ALERTS)
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification", Map.of("payment", payment));
    if (StringUtils.isEmpty(payment.cleanMessage())) {
      sendUsualNotification(payment);
      return;
    }
    if ("philipi4".equals(payment.recipientId())) {
      if (payment.amount().getMajor() == 100) {
        sendNotificationWithGeneratedArt(payment);
      } else {
        sendUsualNotification(payment);
      }
      return;
    }
    if ("batongleba".equals(payment.recipientId())) {
      sendNotificationWithGeneratedArt(payment);
      return;
    }
    sendUsualNotification(payment);
  }
}

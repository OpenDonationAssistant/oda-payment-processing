package io.github.opendonationassistant.alert;

import static io.github.opendonationassistant.rabbit.Queue.Payments.ALERTS;

import io.github.opendonationassistant.art.ArtClient;
import io.github.opendonationassistant.art.ArtGenerationRequest;
import io.github.opendonationassistant.art.OperationDescription;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.events.PaymentSender;
import io.github.opendonationassistant.events.alerts.AlertNotification.AlertMedia;
import io.github.opendonationassistant.events.alerts.AlertSender;
import io.micronaut.context.annotation.Value;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import jakarta.inject.Inject;
import java.util.List;
import java.util.random.RandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RabbitListener
public class AlertPaymentListener {

  private Logger log = LoggerFactory.getLogger(AlertPaymentListener.class);
  private AlertSender sender;
  private ArtClient artClient;
  private String token;
  private String modelUri;
  private ArtGenerationRequest.Options options;

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

  @Queue(ALERTS)
  public void listen(CompletedPaymentNotification payment) {
    log.info("Received notification for alert: {}", payment);
    if (!"tabularussia".equals(payment.getRecipientId())) {
      sender.send(payment.getRecipientId(), payment.toAlertNotification());
      return;
    }
    if (
      !(payment.getAmount().getMajor() > 499 &&
        payment.getAmount().getMajor() < 1000)
    ) {
      sender.send(payment.getRecipientId(), payment.toAlertNotification());
      return;
    }
    log.info("Creating art for {} and token {}", payment.getId(), token);
    var artRequest = new ArtGenerationRequest();
    artRequest.setModelUri(this.modelUri);
    artRequest.setMessages(
      List.of(new ArtGenerationRequest.Message(payment.getMessage(), 1))
    );
    artRequest.setGenerationOptions(this.options);
    var done = false;
    OperationDescription requested = null;
    try {
      requested = artClient.generate(token, artRequest);
      log.info("Requested art: {}", requested.getId());
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
    var notification = payment.toAlertNotification();
    if (done) {
      notification.setMedia(
        new AlertMedia("/generated/%s".formatted(requested.getId()))
      );
    }
    log.info("Sent alert for payment: {}", notification);
    sender.send(payment.getRecipientId(), notification);
  }
}

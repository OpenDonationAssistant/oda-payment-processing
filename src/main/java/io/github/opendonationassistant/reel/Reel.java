package io.github.opendonationassistant.reel;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.events.widget.Widget;
import io.micronaut.data.annotation.Transient;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Reel extends ReelData {

  private ODALogger log = new ODALogger(this);

  public static final String EACH_PAYMENT_CONDITION = "eachpayment";
  public static final String SUM_AMOUNT_CONDITION = "sum";
  public static final String NOOP_CONDITION = "noop";

  @Transient
  private final ReelCommandSender commandSender;

  @Transient
  private final Random random;

  @Transient
  private final ReelRepository repository;

  public Reel(
    ReelCommandSender commandSender,
    ReelRepository repository,
    String recipientId,
    String widgetId
  ) {
    Objects.requireNonNull(commandSender, "ReelCommandSender is required");
    this.setWidgetConfigId(widgetId);
    this.setRecipientId(recipientId);
    this.commandSender = commandSender;
    this.repository = repository;
    this.random = new Random();
    this.setId(Generators.timeBasedEpochGenerator().generate().toString());
    this.setItems(List.of());
    this.setRequiredAmount(new Amount(0, 0, "RUB"));
    this.setAccumulatedAmount(new Amount(0, 0, "RUB"));
  }

  public void handlePayment(CompletedPaymentNotification payment) {
    log.info(
      "Handling payment for reel",
      Map.of("payment", payment, "reel", this, "condition", getCondition(), "amount", getRequiredAmount())
    );
    if (payment == null) {
      return;
    }
    if (
      EACH_PAYMENT_CONDITION.equals(getCondition()) &&
      payment.amount().getMajor() >= getRequiredAmount().getMajor()
    ) {
      var command = new ReelCommand();
      command.setType("trigger");
      command.setSelection(getItems().get(random.nextInt(getItems().size())));
      command.setWidgetId(getWidgetConfigId());
      command.setPaymentId(payment.id());
      command.setRecipientId(payment.recipientId());
      log.info("Send reel command", Map.of("command", command));
      commandSender.send("reel", command);
    }
  }

  public void select(List<String> items) {}

  public void update(Widget widget) {
    setEnabled(widget.enabled());
    widget
      .config()
      .properties()
      .stream()
      .forEach(property -> {
        if ("type".equals(property.name())) {
          this.setCondition((String) property.value());
        }
        if ("optionList".equals(property.name())) {
          this.setItems((List<String>) property.value());
        }
        if ("requiredAmount".equals(property.name())) {
          this.setRequiredAmount(
              new Amount((Integer) property.value(), 0, "RUB")
            );
        }
      });
    log.info("Update reel", Map.of("reel", this));
    repository.update(this);
  }

  @Override
  public String toString() {
    return ToString.asJson(this);
  }
}

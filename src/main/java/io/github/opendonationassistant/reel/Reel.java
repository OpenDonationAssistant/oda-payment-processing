package io.github.opendonationassistant.reel;

import com.fasterxml.uuid.Generators;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.micronaut.data.annotation.Transient;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reel extends ReelData {

  private Logger log = LoggerFactory.getLogger(Reel.class);

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
    log.info("Handling payment: {} for reel: {}", payment, this);
    if (payment == null) {
      return;
    }
    if (
      EACH_PAYMENT_CONDITION.equals(getCondition()) &&
      payment.getAmount().getMajor() >= getRequiredAmount().getMajor()
    ) {
      var command = new ReelCommand();
      command.setType("trigger");
      command.setSelection(getItems().get(random.nextInt(getItems().size())));
      command.setWidgetId(getWidgetConfigId());
      command.setPaymentId(payment.getId());
      command.setRecipientId(payment.getRecipientId());
      log.info("send reel command: {}", command);
      commandSender.send("%sreel".formatted(payment.getRecipientId()), command);
    }
  }

  public void select(List<String> items) {}

  public void update(WidgetConfig config) {
    config
      .getProperties()
      .stream()
      .forEach(property -> {
        if ("type".equals(property.getName())) {
          this.setCondition((String) property.getValue());
        }
        if ("optionList".equals(property.getName())) {
          this.setItems((List<String>) property.getValue());
        }
        if ("requiredAmount".equals(property.getName())) {
          this.setRequiredAmount(
              new Amount((Integer) property.getValue(), 0, "RUB")
            );
        }
      });
    log.info("Update reel to {}", this);
    repository.update(this);
  }

  @Override
  public String toString() {
    return (
      "{\"_type\"=\"Reel\",\"getRecipientId()\"=\"" +
      getRecipientId() +
      "\", getCondition()\"=\"" +
      getCondition() +
      "\", getAccumulatedAmount()\"=\"" +
      getAccumulatedAmount() +
      "\", getRequiredAmount()\"=\"" +
      getRequiredAmount() +
      "\", getItems()\"=\"" +
      getItems() +
      "\", getId()\"=\"" +
      getId() +
      "\", getWidgetConfigId()\"=\"" +
      getWidgetConfigId() +
      "}"
    );
  }
}

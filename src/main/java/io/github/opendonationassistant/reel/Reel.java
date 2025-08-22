package io.github.opendonationassistant.reel;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.events.widget.Widget;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.github.opendonationassistant.events.widget.WidgetProperty;
import io.github.opendonationassistant.events.widget.WidgetUpdateCommand;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Reel {

  private ODALogger log = new ODALogger(this);

  private final ReelData data;
  private final ReelCommandSender commandSender;
  private final ReelDataRepository repository;
  private final Random random;
  private final WidgetCommandSender widgetSender;

  public Reel(
    ReelData data,
    ReelCommandSender commandSender,
    ReelDataRepository repository,
    WidgetCommandSender widgetSender
  ) {
    this.data = data;
    this.commandSender = commandSender;
    this.widgetSender = widgetSender;
    this.repository = repository;
    this.random = new Random();
  }

  public boolean isEnabled() {
    return data.enabled();
  }

  public Reel toggle() {
    final ReelData updatedData = data.withEnabled(!data.enabled());
    repository.update(updatedData);
    return new Reel(updatedData, commandSender, repository, widgetSender);
  }

  public String run() {
    return data.items().get(random.nextInt(data.items().size()));
  }

  public void handlePayment(CompletedPaymentNotification payment) {
    if (payment == null) {
      return;
    }
    if (data.items() == null || data.items().isEmpty()) {
      return;
    }
    if (!data.enabled()) {
      return;
    }
    log.info(
      "Handling payment for reel",
      Map.of("payment", payment, "reel", this)
    );
    if (payment.amount().getMajor() >= data.requiredAmount().getMajor()) {
      var command = new ReelCommand();
      command.setType("trigger");
      command.setSelection(run());
      command.setWidgetId(data.widgetConfigId());
      command.setPaymentId(payment.id());
      command.setRecipientId(payment.recipientId());
      final Integer step = data.stepAmount().getMajor();
      if (step > 0) {
        widgetSender.send(
          new WidgetUpdateCommand(
            data.widgetConfigId(),
            new WidgetConfig(
              List.of(
                new WidgetProperty(
                  "requiredAmount",
                  "widget-reel-required-amount",
                  "number",
                  data.requiredAmount().getMajor() + step
                )
              )
            )
          )
        );
      }
      log.info("Send reel command", Map.of("command", command));
      commandSender.send("reel", command);
    }
  }

  public Reel update(Widget widget) {
    var updatedData = widget
      .config()
      .properties()
      .stream()
      .reduce(
        data,
        (data, property) -> {
          if ("optionList".equals(property.name())) {
            return data.withItems((List<String>) property.value());
          }
          if ("requiredAmount".equals(property.name())) {
            return data.withRequiredAmount(
              new Amount((Integer) property.value(), 0, "RUB")
            );
          }
          if ("stepAmount".equals(property.name())) {
            return data.withStepAmount(
              new Amount((Integer) property.value(), 0, "RUB")
            );
          }
          return data;
        },
        (first, second) -> {
          return first;
        }
      );
    log.info("Update reel", Map.of("data", updatedData));
    repository.update(updatedData);
    return new Reel(updatedData, commandSender, repository, widgetSender);
  }
}

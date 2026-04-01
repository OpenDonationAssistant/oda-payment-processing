package io.github.opendonationassistant.donaton;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.donaton.repository.DonatonData;
import io.github.opendonationassistant.donaton.repository.DonatonDataRepository;
import io.github.opendonationassistant.donaton.repository.DonatonLink;
import io.github.opendonationassistant.donaton.repository.DonatonLinkRepository;
import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.github.opendonationassistant.events.widget.WidgetProperty;
import io.github.opendonationassistant.events.widget.WidgetUpdateCommand;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Donaton {

  private final ODALogger log = new ODALogger(this);
  private DonatonData data;
  private DonatonDataRepository repository;
  private DonatonLinkRepository linkRepository;
  private WidgetCommandSender commandSender;
  private DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

  public Donaton(
    DonatonData data,
    DonatonDataRepository repository,
    DonatonLinkRepository linkRepository,
    WidgetCommandSender commandSender
  ) {
    this.data = data;
    this.repository = repository;
    this.linkRepository = linkRepository;
    this.commandSender = commandSender;
  }

  public void handleChange(Amount addition, String originId) {
    if (!data.enabled()) {
      return;
    }
    var currency = addition.getCurrency();
    var rate = data.secondsPerDonation().get(currency);
    if (rate == null) {
      log.debug(
        "No rate found",
        Map.of("currency", currency, "recipientId", data.recipientId())
      );
      return;
    }
    var amount = addition.getMajor();
    var endDate = data.endDate();
    var change = rate.multiply(BigDecimal.valueOf(amount)).longValue();
    var newEndDate = endDate.plusSeconds(change);
    data = data.withEndDate(newEndDate);
    repository.update(data);
    linkRepository.save(
      new DonatonLink(
        Generators.timeBasedEpochGenerator().generate().toString(),
        data.id(),
        originId,
        "payment",
        endDate,
        newEndDate
      )
    );
    var timerEnd = new WidgetProperty(
      "timer-end",
      "timer-end",
      "",
      Map.of("timestamp", formatter.format(newEndDate))
    );
    var patch = new WidgetConfig(List.of(timerEnd));
    WidgetUpdateCommand command = new WidgetUpdateCommand(data.id(), patch);
    commandSender.send(command);
  }

  public void handlePayment(PaymentEvent payment) {
    handleChange(payment.amount(), payment.id());
  }

  public void update(WidgetConfig config) {
    config
      .properties()
      .stream()
      .forEach(property -> {
        var value = property.value();
        if (value == null) {
          return;
        }
        if ("timer-end".equals(property.name())) {
          String timestamp = ((Map<String, String>) value).get("timestamp");
          this.data = this.data.withEndDate(
              Instant.from(formatter.parse(timestamp))
            );
        }
        if ("price".equals(property.name())) {
          var price = (Map<String, Object>) value;
          String unit = (String) price.get("unit");
          Integer amount = (Integer) price.get("price");
          if (amount == null) {
            return;
          }
          if ("10MIN".equals(unit)) {
            var rate = BigDecimal.valueOf(60 * 10).divide(
              BigDecimal.valueOf(amount),
              5,
              RoundingMode.HALF_UP
            );
            this.data = this.data.withSecondsPerDonation(Map.of("RUB", rate));
          }
          if ("MIN".equals(unit)) {
            var rate = BigDecimal.valueOf(60).divide(
              BigDecimal.valueOf(amount),
              5,
              RoundingMode.HALF_UP
            );
            this.data = this.data.withSecondsPerDonation(Map.of("RUB", rate));
          }
          if ("HOUR".equals(unit)) {
            var rate = BigDecimal.valueOf(60 * 60).divide(
              BigDecimal.valueOf(amount),
              5,
              RoundingMode.HALF_UP
            );
            this.data = this.data.withSecondsPerDonation(Map.of("RUB", rate));
          }
          if ("DAY".equals(unit)) {
            var rate = BigDecimal.valueOf(60 * 60 * 24).divide(
              BigDecimal.valueOf(amount),
              5,
              RoundingMode.HALF_UP
            );
            this.data = this.data.withSecondsPerDonation(Map.of("RUB", rate));
          }
        }
      });
    log.info("Update donaton", Map.of("update", this.data));
    repository.update(this.data);
  }

  public DonatonData data() {
    return this.data;
  }

  public void toggle() {
    this.data = this.data.withEnabled(!this.data.enabled());
    this.repository.update(this.data);
  }
}

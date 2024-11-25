package io.github.opendonationassistant.donaton;

import io.github.opendonationassistant.donaton.repository.DonatonData;
import io.github.opendonationassistant.donaton.repository.DonatonDataRepository;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.github.opendonationassistant.events.widget.WidgetProperty;
import io.github.opendonationassistant.events.widget.WidgetUpdateCommand;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Serdeable
public class Donaton {

  private DonatonData data;
  private DonatonDataRepository repository;
  private WidgetCommandSender commandSender;
  private Logger log = LoggerFactory.getLogger(Donaton.class);
  private DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

  public Donaton(
    DonatonData data,
    DonatonDataRepository repository,
    WidgetCommandSender commandSender
  ) {
    this.data = data;
    this.repository = repository;
    this.commandSender = commandSender;
  }

  public void handlePayment(CompletedPaymentNotification notification) {
    var amount = notification.getAmount().getMajor();
    var currency = notification.getAmount().getCurrency();
    var rate = data.getSecondsPerDonation().get(currency);
    var endDate = data.getEndDate();
    var newEndDate = endDate.plusSeconds(
      rate.multiply(BigDecimal.valueOf(amount)).longValue()
    );
    data.setEndDate(newEndDate);
    repository.update(data);
    var timerEnd = new WidgetProperty();
    timerEnd.setName("timer-end");
    timerEnd.setValue(Map.of("timestamp", formatter.format(newEndDate)));
    var patch = new WidgetConfig();
    patch.setProperties(List.of(timerEnd));
    WidgetUpdateCommand command = new WidgetUpdateCommand(data.getId(), patch);
    commandSender.send(command);
  }

  public void update(WidgetConfig config) {
    config
      .getProperties()
      .stream()
      .forEach(property -> {
        if ("timer-end".equals(property.getName())) {
          String timestamp =
            ((Map<String, String>) property.getValue()).get("timestamp");
          this.data.setEndDate(Instant.from(formatter.parse(timestamp)));
        }
        if ("price".equals(property.getName())) {
          var price = (Map<String, Object>) property.getValue();
          String unit = (String) price.get("unit");
          Integer amount = (Integer) price.get("price");
          if ("10MIN".equals(unit)) {
            var rate = BigDecimal
              .valueOf(60 * 10)
              .divide(BigDecimal.valueOf(amount), RoundingMode.HALF_UP);
            this.data.setSecondsPerDonation(Map.of("RUB", rate));
          }
          if ("MIN".equals(unit)) {
            var rate = BigDecimal
              .valueOf(60)
              .divide(BigDecimal.valueOf(amount), RoundingMode.HALF_UP);
            this.data.setSecondsPerDonation(Map.of("RUB", rate));
          }
          if ("HOUR".equals(unit)) {
            var rate = BigDecimal
              .valueOf(60 * 60)
              .divide(BigDecimal.valueOf(amount), RoundingMode.HALF_UP);
            this.data.setSecondsPerDonation(Map.of("RUB", rate));
          }
          if ("DAY".equals(unit)) {
            var rate = BigDecimal
              .valueOf(60 * 60 * 24)
              .divide(BigDecimal.valueOf(amount), RoundingMode.HALF_UP);
            this.data.setSecondsPerDonation(Map.of("RUB", rate));
          }
        }
      });
    log.info("Update donaton to {}", this.data);
    repository.update(this.data);
  }

  @Override
  public String toString() {
    try {
      return ObjectMapper.getDefault().writeValueAsString(this);
    } catch (Exception e) {
      return "Can't serialize DonatonData: " + e.getMessage();
    }
  }
}

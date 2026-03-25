package io.github.opendonationassistant.reel.repository;

import com.fasterxml.uuid.Generators;
import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.history.event.ReelResultHistoryEvent;
import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.github.opendonationassistant.events.history.HistoryFacade;
import io.github.opendonationassistant.events.widget.Widget;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.jspecify.annotations.Nullable;

public class Reel {

  private ODALogger log = new ODALogger(this);

  private final ReelData data;
  private final HistoryFacade facade;
  private final ReelDataRepository repository;
  private final ReelLinkRepository linkRepository;
  private final Random random;

  public Reel(
    ReelData data,
    HistoryFacade facade,
    ReelDataRepository repository,
    ReelLinkRepository linkRepository
  ) {
    this.data = data;
    this.facade = facade;
    this.repository = repository;
    this.random = new SecureRandom();
    this.linkRepository = linkRepository;
  }

  public Reel toggle() {
    final ReelData updatedData = this.data.withEnabled(!this.data.enabled());
    repository.update(updatedData);
    return new Reel(updatedData, facade, repository, linkRepository);
  }

  public void run(@Nullable String source, @Nullable String originId) {
    var index = random.nextInt(data.items().size());
    var selection = data.items().get(index);
    facade.sendEvent(
      new ReelResultHistoryEvent(
        source,
        originId,
        data.recipientId(),
        data.widgetConfigId(),
        String.valueOf(index), // TODO send reel item id not index
        selection
      )
    );
  }

  public void handlePayment(PaymentEvent payment) {
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
      linkRepository.save(
        new ReelLink(
          Generators.timeBasedEpochGenerator().generate().toString(),
          data.id(),
          payment.id(),
          "payment"
        )
      );
    }
  }

  public Reel update(Widget widget) {
    return Optional.ofNullable(widget.config().properties())
      .map(properties -> {
        var updatedData = properties
          .stream()
          .reduce(
            data,
            (data, property) -> {
              var value = property.value();
              if (value == null) {
                return data;
              }
              if ("optionList".equals(property.name())) {
                return data.withItems((List<String>) value);
              }
              if ("requiredAmount".equals(property.name())) {
                return data.withRequiredAmount(
                  new Amount((Integer) value, 0, "RUB")
                );
              }
              if ("stepAmount".equals(property.name())) {
                return data.withStepAmount(
                  new Amount((Integer) value, 0, "RUB")
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
        return new Reel(updatedData, facade, repository, linkRepository);
      })
      .orElseGet(() -> this);
  }

  public ReelData data() {
    return this.data;
  }
}

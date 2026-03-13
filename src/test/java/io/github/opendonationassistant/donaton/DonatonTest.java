package io.github.opendonationassistant.donaton;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.donaton.repository.DonatonData;
import io.github.opendonationassistant.donaton.repository.DonatonDataRepository;
import io.github.opendonationassistant.donaton.repository.DonatonLinkRepository;
import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
public class DonatonTest {

  @Inject
  DonatonDataRepository repository;

  @Inject
  DonatonLinkRepository linkRepository;

  WidgetCommandSender commandSender = mock(WidgetCommandSender.class);

  private static final String DONATON_ID = "test-donaton-id";
  private static final String RECIPIENT_ID = "testuser";
  private static final Map<String, BigDecimal> RATE = Map.of(
    "RUB",
    BigDecimal.valueOf(10)
  );
  private static final Instant END_DATE = Instant.parse("2022-01-01T00:00:00Z");

  Model<PaymentEvent> paymentEventModel = Instancio.of(PaymentEvent.class)
    .set(field(PaymentEvent::actions), List.of())
    .set(field(PaymentEvent::attachments), List.of())
    .toModel();

  Model<DonatonData> donatonDataModel = Instancio.of(DonatonData.class)
    .set(field(DonatonData::id), DONATON_ID)
    .set(field(DonatonData::recipientId), RECIPIENT_ID)
    .set(field(DonatonData::enabled), true)
    .set(field(DonatonData::endDate), END_DATE)
    .set(field(DonatonData::secondsPerDonation), RATE)
    .toModel();

  @Test
  void shouldHandlePaymentAndUpdateDb() {
    var donatonData = Instancio.of(donatonDataModel).create();
    repository.save(donatonData);

    var donaton = new Donaton(
      donatonData,
      repository,
      linkRepository,
      commandSender
    );

    var payment = Instancio.of(paymentEventModel)
      .set(field(PaymentEvent::recipientId), RECIPIENT_ID)
      .set(field(PaymentEvent::id), "payment-id")
      .set(field(PaymentEvent::amount), new Amount(60, 0, "RUB"))
      .create();

    donaton.handlePayment(payment);

    // + 10 min to end date
    assertEquals(
      Optional.of(
        new DonatonData(
          DONATON_ID,
          RECIPIENT_ID,
          Instant.parse("2022-01-01T00:10:00Z"),
          RATE,
          true
        )
      ),
      repository.findById(DONATON_ID)
    );
    // TODO check that command was sent
  }

  @Test
  void shouldNotHandlePaymentWhenDonatonIsDisabled() {
    var disabledData = Instancio.of(donatonDataModel)
      .set(field(DonatonData::enabled), false)
      .create();
    var donaton = new Donaton(
      disabledData,
      repository,
      linkRepository,
      commandSender
    );

    var payment = Instancio.of(paymentEventModel)
      .set(field(PaymentEvent::recipientId), RECIPIENT_ID)
      .set(field(PaymentEvent::amount), new Amount(300, 0, "RUB"))
      .create();

    donaton.handlePayment(payment);

    verify(commandSender, never()).send(any());
  }
}

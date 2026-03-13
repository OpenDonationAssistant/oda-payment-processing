package io.github.opendonationassistant.reel.listener.handler;

import static org.instancio.Select.field;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.github.opendonationassistant.reel.repository.Reel;
import io.github.opendonationassistant.reel.repository.ReelData;
import io.github.opendonationassistant.reel.repository.ReelRepository;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest(environments = "allinone") // todo construct ObjectMapper with configured serialization and remove MicronautTest
@ExtendWith(InstancioExtension.class)
class PaymentEventHandlerTest {

  @Inject
  ObjectMapper mapper;

  Reel reel = mock(Reel.class);
  ReelRepository reelRepository = mock(ReelRepository.class);

  Model<ReelData> reelDataModel = Instancio.of(ReelData.class)
    .set(field(ReelData::enabled), true)
    .set(field(ReelData::deleted), false)
    .toModel();

  Model<PaymentEvent> paymentEventModel = Instancio.of(PaymentEvent.class)
    .set(field(PaymentEvent::actions), List.of())
    .toModel();

  @Test
  void shouldFindCorrectReelAndCallHandlePaymentOnIt(@Given String recipientId)
    throws IOException {
    PaymentEvent paymentEvent = Instancio.of(paymentEventModel)
      .set(field(PaymentEvent::recipientId), recipientId)
      .create();

    when(reelRepository.findFor(any())).thenReturn(List.of(reel));

    new PaymentEventHandler(reelRepository, mapper).handle(
      mapper.writeValueAsBytes(paymentEvent)
    );

    verify(reelRepository).findFor(recipientId);
    verify(reel).handlePayment(paymentEvent);
  }
}

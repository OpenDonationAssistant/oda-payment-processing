package io.github.opendonationassistant.reel.listener.handler;

import static org.instancio.Select.field;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.github.opendonationassistant.events.reel.ReelFacade;
import io.github.opendonationassistant.reel.repository.Reel;
import io.github.opendonationassistant.reel.repository.ReelRepository;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@MicronautTest
@ExtendWith(InstancioExtension.class)
class PaymentEventHandlerTest {

  @Inject
  ObjectMapper mapper;

  ReelFacade facade = mock(ReelFacade.class);

  ReelRepository reelRepository = mock(ReelRepository.class);

  @Test
  void shouldFindCorrectReelAndCallHandlePaymentOnIt(@Given String recipientId)
    throws IOException {
    PaymentEvent paymentEvent = Instancio.of(PaymentEvent.class)
      .set(field(PaymentEvent::recipientId), recipientId)
      .set(field(PaymentEvent::actions), List.of())
      .create();
    Reel reel = mock(Reel.class);
    when(reelRepository.findFor(recipientId)).thenReturn(List.of(reel));

    new PaymentEventHandler(reelRepository, mapper).handle(
      mapper.writeValueAsBytes(paymentEvent)
    );

    verify(reelRepository).findFor(recipientId);
    verify(reel).handlePayment(paymentEvent);
  }
}

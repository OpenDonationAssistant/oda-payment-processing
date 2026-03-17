package io.github.opendonationassistant.reel.repository;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.logging.ODALogger;
import io.github.opendonationassistant.events.payments.PaymentEvent;
import io.github.opendonationassistant.events.reel.ReelFacade;
import io.github.opendonationassistant.events.widget.WidgetCommandSender;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

public class ReelTest {

  private ODALogger log = new ODALogger(this);

  ReelFacade facade = mock(ReelFacade.class);
  ReelDataRepository repository = mock(ReelDataRepository.class);
  WidgetCommandSender configSender = mock(WidgetCommandSender.class);
  ReelLinkRepository linkRepository = mock(ReelLinkRepository.class);

  private ReelData createReelData() {
    return new ReelData(
      "reelId",
      "testuser",
      "widgetId",
      new Amount(0, 0, "RUB"),
      new Amount(300, 0, "RUB"),
      new Amount(0, 0, "RUB"),
      List.of("item1"),
      true,
      false
    );
  }

  private final PaymentEvent testEvent = Instancio.of(PaymentEvent.class)
    .set(field(PaymentEvent::recipientId), "testuser")
    .set(field(PaymentEvent::amount), new Amount(300, 0, "RUB"))
    .create();

  @Test
  public void testSendingCommandWhenTriggerForEachPayment() {
    var reel = new Reel(createReelData(), facade, repository, linkRepository);

    reel.handlePayment(testEvent);

    verify(linkRepository).save(
      argThat(link -> {
        return (
          "payment".equals(link.source()) &&
          "reelId".equals(link.reelId()) &&
          testEvent.id().equals(link.originId())
        );
      })
    );
  }

  @Test
  public void testSavingCorrectStateToDBWhenToggling() {
    var reel = new Reel(createReelData(), facade, repository, linkRepository);
    reel.toggle();
    verify(repository).update(createReelData().withEnabled(false));
  }

  @Test
  public void testDisabledReelDontSendTriggerCommand() {
    var reel = new Reel(
      createReelData().withEnabled(false),
      facade,
      repository,
      linkRepository
    );

    reel.handlePayment(testEvent);

    verify(linkRepository, org.mockito.Mockito.never()).save(any());
  }
}


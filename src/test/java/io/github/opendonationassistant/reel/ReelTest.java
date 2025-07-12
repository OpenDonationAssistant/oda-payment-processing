package io.github.opendonationassistant.reel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.events.CompletedPaymentNotification;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ReelTest {

  @Test
  public void testSendingCommandWhenTriggerForEachPayment() {
    var commandSender = mock(ReelCommandSender.class);
    var repository = mock(ReelDataRepository.class);

    var reel = new Reel(
      new ReelData(
        "id",
        "testuser",
        ReelData.EACH_PAYMENT_CONDITION,
        "widgetId",
        new Amount(0, 0, "RUB"),
        new Amount(300, 0, "RUB"),
        List.of("test1"),
        true
      ),
      commandSender,
      repository
    );

    var expectedCommand = new ReelCommand();
    expectedCommand.setType(ReelCommand.TRIGGER_TYPE);
    expectedCommand.setPaymentId("id");
    expectedCommand.setSelection("test1");
    expectedCommand.setWidgetId("widgetId");
    expectedCommand.setRecipientId("testuser");

    var notification = new CompletedPaymentNotification(
      "id",
      "nickname",
      "cleanNickname",
      "message",
      "cleanMessage",
      "testuser",
      new Amount(500, 0, "RUB"),
      List.of(),
      "goal",
      Instant.now()
    );

    reel.handlePayment(notification);
    verify(commandSender).send("reel", expectedCommand);
  }
}

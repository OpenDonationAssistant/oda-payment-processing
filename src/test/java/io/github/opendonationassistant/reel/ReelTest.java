package io.github.opendonationassistant.reel;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

public class ReelTest {

  @Test
  public void testSendingCommandWhenTriggerForEachPayment() {
      var commandSender = mock(ReelCommandSender.class);
      var repository = mock(ReelRepository.class);

      var reel = new Reel(commandSender, repository, "testuser", "widgetId");
      reel.setAccumulatedAmount(new Amount(0,0,"RUB"));
      reel.setRequiredAmount(new Amount(300, 0, "RUB"));
      reel.setCondition(Reel.EACH_PAYMENT_CONDITION);
      reel.setItems(List.of("test1"));

      var expectedCommand = new ReelCommand();
      expectedCommand.setType(ReelCommand.TRIGGER_TYPE);
      expectedCommand.setSelection("test1");
      expectedCommand.setWidgetId("widgetId");

      var notification = new CompletedPaymentNotification();
      notification.setRecipientId("testuser");
      notification.setAmount(new Amount(500, 0, "RUB"));

      reel.handlePayment(notification);
      verify(commandSender).send("testuserreel", expectedCommand);
  }

}

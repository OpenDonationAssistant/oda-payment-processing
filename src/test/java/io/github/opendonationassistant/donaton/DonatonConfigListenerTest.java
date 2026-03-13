package io.github.opendonationassistant.donaton;

import static org.junit.jupiter.api.Assertions.assertFalse;

import io.github.opendonationassistant.donaton.repository.DonatonRepository;
import io.github.opendonationassistant.events.widget.Widget;
import io.github.opendonationassistant.events.widget.WidgetChangedEvent;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
public class DonatonConfigListenerTest {

  @Inject
  private DonatonRepository repository;

  @Inject
  private ConfigListener listener;

  @Test
  public void testTogglingDonaton() {
    var config = new WidgetConfig(List.of());
    var widget = new Widget(
      "id",
      "donaton",
      0,
      "testreel",
      true,
      "testuser",
      config
    );
    var event = new WidgetChangedEvent("toggled", widget);
    listener.listen(event);
    final Donaton donaton = repository.byId("testuser", "id");
    assertFalse(donaton.data().enabled());
  }
}

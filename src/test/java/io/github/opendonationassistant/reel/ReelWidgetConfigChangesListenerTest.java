package io.github.opendonationassistant.reel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.opendonationassistant.events.widget.Widget;
import io.github.opendonationassistant.events.widget.WidgetChangedEvent;
import io.github.opendonationassistant.events.widget.WidgetConfig;
import io.github.opendonationassistant.events.widget.WidgetProperty;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.Test;

@MicronautTest(environments = "allinone")
public class ReelWidgetConfigChangesListenerTest {

  @Inject
  ReelRepository reels;

  @Inject
  ReelWidgetConfigChangesListener listener;

  @Test
  public void testTogglingReel() {
    var optionsProperty = new WidgetProperty(
      "optionList",
      "",
      "",
      List.of("1", "2", "3")
    );
    var requiredAmountProperty = new WidgetProperty(
      "requiredAmount",
      "",
      "",
      100
    );
    var config = new WidgetConfig(
      List.of(optionsProperty, requiredAmountProperty)
    );
    var widget = new Widget(
      "id",
      "reel",
      0,
      "testreel",
      true,
      "testuser",
      config
    );
    reels.create(widget);
    final List<Reel> createdReels = reels.findFor("testuser");
    assertEquals(1, createdReels.size());
    assertTrue(createdReels.get(0).isEnabled());
    var event = new WidgetChangedEvent("toggled", widget);
    listener.listen(event);
    final List<Reel> updatedReels = reels.findFor("testuser");
    assertEquals(1, updatedReels.size());
    assertFalse(updatedReels.get(0).isEnabled());
  }
}

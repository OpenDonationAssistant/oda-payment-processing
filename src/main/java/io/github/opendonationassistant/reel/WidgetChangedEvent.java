package io.github.opendonationassistant.reel;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class WidgetChangedEvent {

  private String type;

  private Widget widget;

  public WidgetChangedEvent(String type, Widget widget) {
    this.type = type;
    this.widget = widget;
  }

  public String getType() {
    return type;
  }

  public Widget getWidget() {
    return widget;
  }

  @Override
  public String toString() {
    return "{\"_type\"=\"WidgetChangedEvent\",\"type\"=\"" + type + "\", widget\"=\"" + widget + "}";
  }
}


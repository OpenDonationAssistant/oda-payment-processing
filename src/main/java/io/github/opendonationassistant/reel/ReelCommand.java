package io.github.opendonationassistant.reel;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ReelCommand {

  public static final String TRIGGER_TYPE = "trigger";

  private String type;
  private String selection;
  private String widgetId;
  private String paymentId;
  private String recipientId;

  public String getWidgetId() {
    return widgetId;
  }

  public void setWidgetId(String widgetId) {
    this.widgetId = widgetId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSelection() {
    return selection;
  }

  public void setSelection(String selection) {
    this.selection = selection;
  }

  public String getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(String paymentId) {
    this.paymentId = paymentId;
  }

  public String getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((selection == null) ? 0 : selection.hashCode());
    result = prime * result + ((widgetId == null) ? 0 : widgetId.hashCode());
    result = prime * result + ((paymentId == null) ? 0 : paymentId.hashCode());
    result =
      prime * result + ((recipientId == null) ? 0 : recipientId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ReelCommand other = (ReelCommand) obj;
    if (type == null) {
      if (other.type != null) return false;
    } else if (!type.equals(other.type)) return false;
    if (selection == null) {
      if (other.selection != null) return false;
    } else if (!selection.equals(other.selection)) return false;
    if (widgetId == null) {
      if (other.widgetId != null) return false;
    } else if (!widgetId.equals(other.widgetId)) return false;
    if (paymentId == null) {
      if (other.paymentId != null) return false;
    } else if (!paymentId.equals(other.paymentId)) return false;
    if (recipientId == null) {
      if (other.recipientId != null) return false;
    } else if (!recipientId.equals(other.recipientId)) return false;
    return true;
  }
}

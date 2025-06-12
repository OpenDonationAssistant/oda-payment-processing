package io.github.opendonationassistant.reel;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.ToString;
import io.github.opendonationassistant.utils.StringListConverter;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
@MappedEntity("reel")
public class ReelData {

  @Id
  private String id;
  private String recipientId;
  private String condition;
  private String widgetConfigId;
  private Amount accumulatedAmount;
  private Amount requiredAmount;
  @MappedProperty(converter = StringListConverter.class)
  private List<String> items;
  private Boolean enabled;

  public String getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(String recipientId) {
    this.recipientId = recipientId;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public Amount getAccumulatedAmount() {
    return accumulatedAmount;
  }

  public void setAccumulatedAmount(Amount accumulatedAmount) {
    this.accumulatedAmount = accumulatedAmount;
  }

  public Amount getRequiredAmount() {
    return requiredAmount;
  }

  public void setRequiredAmount(Amount requiredAmount) {
    this.requiredAmount = requiredAmount;
  }

  public List<String> getItems() {
    return items;
  }

  public void setItems(List<String> items) {
    this.items = items;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getWidgetConfigId() {
    return widgetConfigId;
  }

  public void setWidgetConfigId(String widgetConfigId) {
    this.widgetConfigId = widgetConfigId;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public String toString() {
    return ToString.asJson(this);
  }
}

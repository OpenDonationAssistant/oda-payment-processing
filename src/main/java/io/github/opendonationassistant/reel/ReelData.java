package io.github.opendonationassistant.reel;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.utils.StringListConverter;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
@MappedEntity("reel")
public record ReelData(
  @Id String id,
  String recipientId,
  String condition,
  String widgetConfigId,
  Amount accumulatedAmount,
  Amount requiredAmount,
  @MappedProperty(converter = StringListConverter.class) List<String> items,
  Boolean enabled
) {
  public static final String EACH_PAYMENT_CONDITION = "eachpayment";
  public static final String SUM_AMOUNT_CONDITION = "sum";
  public static final String NOOP_CONDITION = "noop";

  public ReelData withEnabled(boolean value) {
    return new ReelData(
      id,
      recipientId,
      condition,
      widgetConfigId,
      accumulatedAmount,
      requiredAmount,
      items,
      value
    );
  }

  public ReelData withItems(List<String> value) {
    return new ReelData(
      id,
      recipientId,
      condition,
      widgetConfigId,
      accumulatedAmount,
      requiredAmount,
      value,
      enabled
    );
  }

  public ReelData withRequiredAmount(Amount value) {
    return new ReelData(
      id,
      recipientId,
      condition,
      widgetConfigId,
      accumulatedAmount,
      value,
      items,
      enabled
    );
  }
}

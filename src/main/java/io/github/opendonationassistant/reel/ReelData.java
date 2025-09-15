package io.github.opendonationassistant.reel;

import io.github.opendonationassistant.commons.Amount;
import io.github.opendonationassistant.commons.StringListConverter;
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
  String widgetConfigId,
  Amount accumulatedAmount,
  Amount requiredAmount,
  Amount stepAmount,
  @MappedProperty(converter = StringListConverter.class) List<String> items,
  Boolean enabled,
  Boolean deleted
) {
  public static final String EACH_PAYMENT_CONDITION = "eachpayment";
  public static final String SUM_AMOUNT_CONDITION = "sum";
  public static final String NOOP_CONDITION = "noop";

  public ReelData withEnabled(boolean value) {
    return new ReelData(
      id,
      recipientId,
      widgetConfigId,
      accumulatedAmount,
      requiredAmount,
      stepAmount,
      items,
      value,
      deleted
    );
  }

  public ReelData withItems(List<String> value) {
    return new ReelData(
      id,
      recipientId,
      widgetConfigId,
      accumulatedAmount,
      requiredAmount,
      stepAmount,
      value,
      enabled,
      deleted
    );
  }

  public ReelData withRequiredAmount(Amount value) {
    return new ReelData(
      id,
      recipientId,
      widgetConfigId,
      accumulatedAmount,
      value,
      stepAmount,
      items,
      enabled,
      deleted
    );
  }

  public ReelData withStepAmount(Amount value) {
    return new ReelData(
      id,
      recipientId,
      widgetConfigId,
      accumulatedAmount,
      requiredAmount,
      value,
      items,
      enabled,
      deleted
    );
  }

  public ReelData withDeleted(Boolean value) {
    return new ReelData(
      id,
      recipientId,
      widgetConfigId,
      accumulatedAmount,
      requiredAmount,
      stepAmount,
      items,
      enabled,
      value
    );
  }
}

package io.github.opendonationassistant.reel;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.data.model.DataType;

@Serdeable
@TypeDef(type = DataType.STRING, converter = AmountConverter.class)
public class Amount {

  private Integer major;
  private Integer minor;
  private String currency;

  public Amount(Integer major, Integer minor, String currency) {
    this.minor = minor;
    this.major = major;
    this.currency = currency;
  }

  public Integer getMinor() {
    return minor;
  }

  public Integer getMajor() {
    return major;
  }

  public String getCurrency() {
    return currency;
  }

  @Override
  public String toString() {
    return (
      "Amount [minor=" +
      minor +
      ", major=" +
      major +
      ", currency=" +
      currency +
      "]"
    );
  }
}

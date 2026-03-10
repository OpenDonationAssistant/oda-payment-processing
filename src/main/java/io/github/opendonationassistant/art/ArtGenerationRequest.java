package io.github.opendonationassistant.art;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record ArtGenerationRequest(
  @JsonProperty("model_uri") String modelUri,
  List<Message> messages,
  @JsonProperty("generation_options") Options generationOptions
) {
  @Serdeable
  public static record Message(String text, Integer weight) {}

  @Serdeable
  public static record Options(
    @JsonProperty("mime_type") String mimeType,
    String seed
  ) {}
}

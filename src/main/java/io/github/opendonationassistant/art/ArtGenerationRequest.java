package io.github.opendonationassistant.art;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public class ArtGenerationRequest {

  @JsonProperty("model_uri")
  private String modelUri;

  private List<Message> messages;

  @JsonProperty("generation_options")
  private Options generationOptions;

  @Serdeable
  public static record Message(String text, Integer weight) {}

  @Serdeable
  public static class Options {

    @JsonProperty("mime_type")
    private String mimeType;

    private String seed;

    public String getMimeType() {
      return mimeType;
    }

    public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
    }

    public String getSeed() {
      return seed;
    }

    public void setSeed(String seed) {
      this.seed = seed;
    }
  }

  public String getModelUri() {
    return modelUri;
  }

  public void setModelUri(String modelUri) {
    this.modelUri = modelUri;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public Options getGenerationOptions() {
    return generationOptions;
  }

  public void setGenerationOptions(Options generationOptions) {
    this.generationOptions = generationOptions;
  }
}

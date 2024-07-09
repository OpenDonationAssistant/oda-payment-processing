package io.github.opendonationassistant;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.ApplicationContextConfigurer;
import io.micronaut.context.annotation.ContextConfigurer;
import io.micronaut.rabbitmq.connect.ChannelInitializer;
import io.micronaut.runtime.Micronaut;
import jakarta.inject.Singleton;

public class Application {

  @ContextConfigurer
  public static class DefaultEnvironmentConfigurer
    implements ApplicationContextConfigurer {

    @Override
    public void configure(ApplicationContextBuilder builder) {
      builder.defaultEnvironments("standalone");
    }
  }

  public static void main(String[] args) {
    ApplicationContext context = Micronaut
      .build(args)
      .mainClass(Application.class)
      .banner(false)
      .start();
    Beans.context = context;
  }

  @Singleton
  public ChannelInitializer rabbitConfiguration(){
    return new RabbitConfiguration();
  }
}

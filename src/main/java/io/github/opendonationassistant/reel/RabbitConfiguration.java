package io.github.opendonationassistant.reel;

import com.rabbitmq.client.Channel;
import io.micronaut.rabbitmq.connect.ChannelInitializer;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.HashMap;

@Singleton
public class RabbitConfiguration extends ChannelInitializer {

    @Override
    public void initialize(Channel channel, String name) throws IOException {
        channel.queueDeclare("commands.reel", true, false, false, new HashMap<>());
        channel.queueBind("commands.reel", "commands", "reel");
    }

}

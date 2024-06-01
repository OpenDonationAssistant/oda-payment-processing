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
        channel.queueDeclare("payments_for_reel", true, false, false, new HashMap<>());
        channel.queueBind("payments_for_reel", "amq.topic", "payments");
        channel.queueDeclare("payments_for_goal", true, false, false, new HashMap<>());
        channel.queueBind("payments_for_goal", "amq.topic", "payments");
        channel.queueDeclare("payments_for_history", true, false, false, new HashMap<>());
        channel.queueBind("payments_for_history", "amq.topic", "payments");
    }

}

package ru.itis.javalab.jlmq.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.util.function.Supplier;

import static ru.itis.javalab.jlmq.sdk.JLMQ.MAPPER;

@ClientEndpoint
public class Connector {

    private final Endpoint endpoint;
    private Consumer consumer;

    Connector(URI uri) {
        try {
            endpoint = new Endpoint(this);
            ContainerProvider.getWebSocketContainer().connectToServer(endpoint, uri);
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Producer producer(String queue) {
        return new Producer(this, queue);
    }

    private synchronized Consumer createConsumer(String queue, Supplier<Consumer> supplier) {
        if (consumer != null) {
            throw new IllegalStateException("consumer was already created");
        }

        consumer = supplier.get();
        send(new SubscribeMessage(queue));

        return consumer;
    }

    public Consumer consumer(String queue, MessageHandler<JsonNode> handler) {
        return createConsumer(queue, () -> new Consumer(this, handler));
    }

    public <T> Consumer consumer(String queue, Class<T> clazz, MessageHandler<T> handler) {
        return createConsumer(queue, () -> new Consumer(this, handler, clazz));
    }

    void send(Object message) {
        try {
            endpoint.send(MAPPER.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    Consumer getConsumer() {
        return consumer;
    }

    static class SubscribeMessage {
        private final String queue;

        public SubscribeMessage(String queue) {
            this.queue = queue;
        }

        public String getCommand() {
            return "subscribe";
        }

        public String getQueue() {
            return queue;
        }
    }
}

package ru.itis.javalab.jlmq.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.util.function.Supplier;

import static ru.itis.javalab.jlmq.sdk.JLMQ.MAPPER;

/**
 * Class for managing websocket connection.
 */
public class Connector {

    private final Endpoint endpoint;
    private Consumer consumer;
    private boolean isClosed;

    Connector(URI uri) {
        endpoint = new Endpoint(this);
        isClosed = false;

        try {
            ContainerProvider.getWebSocketContainer().connectToServer(endpoint, uri);
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates new {@code Producer} for specified queue.
     * @throws IllegalStateException if connection is closed
     */
    public Producer producer(String queue) {
        if (isClosed) {
            throw new IllegalStateException("connection is closed");
        }

        return new Producer(this, queue);
    }

    private synchronized Consumer createConsumer(String queue, Supplier<Consumer> supplier) {
        if (isClosed) {
            throw new IllegalStateException("connection is closed");
        }

        if (consumer != null) {
            throw new IllegalStateException("consumer was already created");
        }

        consumer = supplier.get();
        send(new SubscribeMessage(queue));

        return consumer;
    }

    /**
     * Creates {@code Consumer} for specified queue, handling plain {@code JsonNode} objects.
     * Also creates non-daemon forever-running Thread to keep JVM running until {@link #close()} is called.
     *
     * @throws IllegalStateException if consumer was already created or connection is already closed
     */
    public Consumer consumer(String queue, MessageHandler<JsonNode> handler) {
        return createConsumer(queue, () -> new Consumer(this, handler));
    }

    /**
     * Creates {@code Consumer} for specified queue, handling {@code T} message objects.
     * The {@code T} class must have no-args constructor and setters for every property to be parsed correctly.
     * Also creates non-daemon forever-running Thread to keep JVM running until {@link #close()} is called.
     *
     * @throws IllegalStateException if consumer was already created or connection is already closed
     */
    public <T> Consumer consumer(String queue, Class<T> clazz, MessageHandler<T> handler) {
        return createConsumer(queue, () -> new Consumer(this, handler, clazz));
    }

    /**
     * Closes websocket connection (and stops the keep-alive thread if {@code Consumer} was created).
     */
    public synchronized void close() {
        if (isClosed) return;

        isClosed = true;
        endpoint.close();

        if (consumer != null) {
            consumer.stop();
        }
    }

    void send(Object message) {
        if (isClosed) {
            throw new IllegalStateException("connection is closed");
        }

        try {
            endpoint.send(MAPPER.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            close();
            throw new IllegalArgumentException(e);
        }
    }

    Consumer getConsumer() {
        return consumer;
    }

    // "unused" getter are required for Jackson
    @SuppressWarnings("unused")
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

package ru.itis.javalab.jlmq.sdk;

import javax.websocket.*;
import java.io.IOException;

/**
 * Used internally as a websocket endpoint.
 */
@ClientEndpoint
public class Endpoint {

    private final Connector connector;
    private Session session;

    public Endpoint(Connector connector) {
        this.connector = connector;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        Consumer consumer = connector.getConsumer();
        if (consumer == null) {
            throw new IllegalStateException("consumer is not instantiated");
        }

        consumer.handle(message);
    }

    public void send(String text) {
        if (session == null) {
            throw new IllegalStateException();
        }

        session.getAsyncRemote().sendText(text);
    }

    @OnClose
    public void onClose() {
        connector.close();
    }

    void close() {
        try {
            session.close();
        } catch (IOException ignore) {}
    }
}

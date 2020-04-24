package ru.itis.javalab.jlmq.sdk;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

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
}

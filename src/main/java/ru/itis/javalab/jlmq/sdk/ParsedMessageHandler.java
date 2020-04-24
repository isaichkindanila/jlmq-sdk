package ru.itis.javalab.jlmq.sdk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

import static ru.itis.javalab.jlmq.sdk.JLMQ.MAPPER;

class ParsedMessageHandler<T> implements MessageHandler<JsonNode> {

    private final MessageHandler<T> handler;
    private final Class<? extends T> messageClass;

    ParsedMessageHandler(MessageHandler<T> handler, Class<? extends T> messageClass) {
        this.handler = Objects.requireNonNull(handler);
        this.messageClass = Objects.requireNonNull(messageClass);
    }

    @Override
    public void handle(JsonNode body) throws Exception {
        try {
            handler.handle(MAPPER.convertValue(body, messageClass));
        } catch (IllegalArgumentException e) {
            throw new MalformedMessageException();
        }
    }
}

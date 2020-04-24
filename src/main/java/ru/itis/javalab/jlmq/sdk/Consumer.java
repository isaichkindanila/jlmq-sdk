package ru.itis.javalab.jlmq.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import static ru.itis.javalab.jlmq.sdk.JLMQ.MAPPER;

public class Consumer {

    private final Connector connector;
    private final MessageHandler<JsonNode> handler;

    Consumer(Connector connector, MessageHandler<JsonNode> handler) {
        this.connector = connector;
        this.handler = handler;
    }

    <T> Consumer(Connector connector, MessageHandler<T> handler, Class<? extends T> messageClass) {
        this.connector = connector;
        this.handler = new ParsedMessageHandler<>(handler, messageClass);
    }

    void handle(String message) {
        JsonNode root;

        try {
            root = MAPPER.readTree(message);
        } catch (JsonProcessingException e) {
            // TODO: stop this consumer
            throw new RuntimeException("malformed JSON from server - should never happen", e);
        }

        String command = root.get("command").asText();
        if (!"receive".equals(command)) {
            // TODO: stop this consumer
            throw new RuntimeException("malformed JSON from server - should never happen");
        }

        String token = root.get("message").asText();
        connector.send(ConsumerMessage.acknowledge(token));

        try {
            handler.handle(root.get("body"));
            connector.send(ConsumerMessage.completed(token));
        } catch (MalformedMessageException e) {
            connector.send(ConsumerMessage.malformed(token));
        } catch (Exception e) {
            // TODO: stop this consumer
        }
    }

    static class ConsumerMessage {
        private final String command;
        private final String message;

        static ConsumerMessage acknowledge(String message) {
            return new ConsumerMessage("acknowledge", message);
        }

        static ConsumerMessage malformed(String message) {
            return new ConsumerMessage("malformed", message);
        }

        static ConsumerMessage completed(String message) {
            return new ConsumerMessage("completed", message);
        }

        private ConsumerMessage(String command, String message) {
            this.command = command;
            this.message = message;
        }

        public String getCommand() {
            return command;
        }

        public String getMessage() {
            return message;
        }
    }
}

package test;

import ru.itis.javalab.jlmq.sdk.Connector;
import ru.itis.javalab.jlmq.sdk.Consumer;
import ru.itis.javalab.jlmq.sdk.JLMQ;
import ru.itis.javalab.jlmq.sdk.Producer;

public class Main {

    public static void main(String[] args) throws Throwable {
        Connector connector = JLMQ.connector("ws://localhost:8080/jlmq");

        Producer producer = connector.producer("test/sdk");
        Consumer consumer = connector.consumer("test/sdk", Message.class, message -> {
            System.out.println("received message: " + message.getText());
            Thread.sleep(500);
        });

        producer.send(new Message("hello"));
        producer.send(new Message("world"));
        producer.send(new Message("!"));

        Thread.sleep(5000);
    }

    static class Message {
        private String text;

        // no-args constructor for Jackson
        public Message() {
        }

        Message(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

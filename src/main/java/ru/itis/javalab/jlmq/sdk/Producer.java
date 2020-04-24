package ru.itis.javalab.jlmq.sdk;

public class Producer {

    private final Connector connector;
    private final String queue;

    public Producer(Connector connector, String queue) {
        this.connector = connector;
        this.queue = queue;
    }

    public void send(Object message) {
        connector.send(new ProducerMessage(queue, message));
    }

    // "unused" getter are required for Jackson
    @SuppressWarnings("unused")
    static class ProducerMessage {
        private final String queue;
        private final Object body;

        public ProducerMessage(String queue, Object body) {
            this.queue = queue;
            this.body = body;
        }

        public String getCommand() {
            return "send";
        }

        public String getQueue() {
            return queue;
        }

        public Object getBody() {
            return body;
        }
    }
}

package ru.itis.javalab.jlmq.sdk;

/**
 * Class publishing messages to a queue.
 */
public class Producer {

    private final Connector connector;
    private final String queue;

    Producer(Connector connector, String queue) {
        this.connector = connector;
        this.queue = queue;
    }

    /**
     * Publishes message to a queue.
     * @param message will be converted to JSON by Jackson
     * @throws IllegalStateException if connection is closed
     */
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

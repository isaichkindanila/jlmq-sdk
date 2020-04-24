package ru.itis.javalab.jlmq.sdk;

/**
 * Lambda-interface for handling messages of various types.
 */
@FunctionalInterface
public interface MessageHandler<T> {
    /**
     * @throws Exception if something went wrong - websocket connection will be closed
     */
    void handle(T body) throws Exception;
}

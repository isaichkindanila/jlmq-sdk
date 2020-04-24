package ru.itis.javalab.jlmq.sdk;

@FunctionalInterface
public interface MessageHandler<T> {
    void handle(T body) throws Exception;
}

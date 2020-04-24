package ru.itis.javalab.jlmq.sdk;

/**
 * Thrown by {@link Consumer} or {@link MessageHandler} if message is malformed.
 *
 * Causes server to mark the message as {@code MALFORMED} (it will not be processed in the future).
 */
public class MalformedMessageException extends RuntimeException {
}

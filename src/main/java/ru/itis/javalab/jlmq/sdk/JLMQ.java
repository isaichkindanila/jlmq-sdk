package ru.itis.javalab.jlmq.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;

/**
 * Class existing for the sole purpose of avoiding usage of {@code new} keyword.
 */
public class JLMQ {

    static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Creates {@code Connector} to specified (by URI) JLMQ server.
     */
    public static Connector connector(String uri) {
        return connector(URI.create(uri));
    }

    /**
     * Creates {@code Connector} to specified (by URI) JLMQ server.
     */
    public static Connector connector(URI uri) {
        return new Connector(uri);
    }

    private JLMQ() {}
}

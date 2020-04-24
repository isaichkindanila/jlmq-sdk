package ru.itis.javalab.jlmq.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;

public class JLMQ {

    static ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static Connector connector(String uri) {
        return connector(URI.create(uri));
    }

    public static Connector connector(URI uri) {
        return new Connector(uri);
    }

    private JLMQ() {}
}

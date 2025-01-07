package com.web_socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import org.openapitools.jackson.nullable.JsonNullableModule;

public record WebSocketMessage(Type type, Object message) {

    public enum Type {
        GET,
        GET_LIST,
        CREATE,
        UPDATE,
        DELETE,
        UNRECOGNIZED
    }

    public <T> T getMessageAs(Class<T> targetType) {
        if (message instanceof LinkedHashMap) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JsonNullableModule());
            return objectMapper.convertValue(message, targetType);
        } else {
            return targetType.cast(message);
        }
    }
}

package com.account.service.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonSerializer<T> implements Serializer<T> {

    private final ObjectMapper mapper;
    private final Class<T> instanceType;

    public JsonSerializer(Class<T> instanceType) {
        this.mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.instanceType = instanceType;
    }

    @Override
    public byte[] serialize(Object instance) throws IOException {
        return mapper.writeValueAsBytes(instance);
    }

    @Override
    public T deserialize(byte[] data) throws IOException {
        return mapper.readValue(data, instanceType);
    }
}

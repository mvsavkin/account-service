package com.account.service.serialization;

import java.io.IOException;

public interface Serializer<T> {

    byte[] serialize(T instance) throws IOException;

    T deserialize(byte[] data) throws IOException;
}
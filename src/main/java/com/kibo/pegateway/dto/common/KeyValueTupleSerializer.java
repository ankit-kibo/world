package com.kibo.pegateway.dto.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.mozu.api.contracts.paymentservice.extensibility.v1.KeyValueTuple;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Overrides the KeyValueTuple serializer to read from a map.
 */
public class KeyValueTupleSerializer extends JsonSerializer<Map<String, Object>> {
    @Override
    public void serialize(Map<String, Object> stringObjectMap, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if(stringObjectMap == null)
            stringObjectMap = new HashMap<>();
        KeyValueTuple[] array = new KeyValueTuple[stringObjectMap.size()];
        int i = 0;
        for(String key: stringObjectMap.keySet()) {
            KeyValueTuple kvt = new KeyValueTuple();
            kvt.setKey(key);
            kvt.setValue(stringObjectMap.get(key));
            array[i++] = kvt;
        }
        jsonGenerator.writeObject(array);
    }
}

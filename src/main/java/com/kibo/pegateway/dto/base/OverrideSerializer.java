package com.kibo.pegateway.dto.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Overrides the serializer for a specific class.
 * @param <R> The class to produce instead of the original class.
 */
public class OverrideSerializer<R extends Object> extends JsonSerializer<R> {
    /*
     * Contains the original serializer.
     */
    JsonSerializer<Object> defaultSerializer;

    /**
     * Set the original serializer.
     * @param defaultSerializer The original serializer
     */
    public void init(JsonSerializer<Object> defaultSerializer) {
        this.defaultSerializer = defaultSerializer;
    }

    @Override
    public void serialize(R target, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        try {
            preProcess(target);
        }
        catch (Exception e) {
            throw new IOException("Error preprocessing for class '" + target.getClass().getName() + "'.", e);
        }
        defaultSerializer.serialize(target, jsonGenerator, serializerProvider);
    }

    /**
     * Called to have the overriden class to assing values
     * into the map(s) from properties.
     * @param object The object to process.
     * @throws Exception Thrown on any error.
     */
    protected void preProcess(R object) throws Exception {
        if(IHasMap.class.isAssignableFrom(object.getClass())) {
            ((IHasMap)object).addPropertiesToMap();
        }
    }
}

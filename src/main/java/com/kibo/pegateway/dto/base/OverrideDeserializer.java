package com.kibo.pegateway.dto.base;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Overridden deserializer to select a different class based on the contents of ESerializers
 * so that overridden classes can be substituted in contract classes.
 * @param <R> The type of the target class that will replace the source class.
 */
@Slf4j
public class OverrideDeserializer<R extends Object> extends JsonDeserializer<R> {
    /*
     * Contains the default deserializer to chain to.
     */
    JsonDeserializer<Object> defaultDeserializer;
    /*
     * Contains the class to substitute.
     */
    Class targetClass;

    /**
     * Called to set the default deserializer and target class.
     * @param defaultDeserializer The default deserializer to chain to.
     * @param targetClass The target class to substitute.
     */
    public void init(JsonDeserializer<Object> defaultDeserializer, Class targetClass) {
        this.defaultDeserializer = defaultDeserializer;
        this.targetClass = targetClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
            String json = jsonParser.readValueAsTree().toString();
            objectMapper.registerModule(new JodaModule());
            R ret = (R)objectMapper.readValue(json, (Class<Object>) targetClass);
            postProcess(ret);
            return ret;
        }
        catch (Exception ex) {
            throw new IOException("Failed deserializing.", ex);
        }
    }

    /**
     * Handle any mapping from maps to properties.
     * @param object The target class object to process.
     * @throws Exception Thrown on any error, most likely missing or invalid properties.
     */
    protected void postProcess(R object) throws Exception {
        if(IHasMap.class.isAssignableFrom(object.getClass())) {
            ((IHasMap)object).setPropertiesFromMap();
        }
    }
}

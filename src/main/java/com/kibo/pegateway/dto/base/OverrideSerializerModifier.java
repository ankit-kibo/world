package com.kibo.pegateway.dto.base;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Overrides serialization for classes in ESerializers.
 */
public class OverrideSerializerModifier extends BeanSerializerModifier {
    static Logger logger = Logger.getLogger(OverrideSerializerModifier.class.getSimpleName());

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        Class clazz = beanDesc.getBeanClass();
        ESerializers eSerializer = ESerializers.getSerializer(clazz);
        if (eSerializer == ESerializers.DefaultSerializer) {
            return serializer;
        }
        try {
            OverrideSerializer overrideSerializer = new OverrideSerializer();
            overrideSerializer.init(serializer);
            return overrideSerializer;
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Missing or invalid serializer.", e);
            return serializer;
        }
    }
}

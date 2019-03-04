package com.kibo.pegateway.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kibo.pegateway.dto.base.OverrideDeserializerModifier;
import com.kibo.pegateway.dto.base.OverrideSerializerModifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {
    /**
     * This sets up the modifiers that change
     * the Jackson behavior.
     * @return The mapper builder.
     */
    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.setSerializerModifier(new OverrideSerializerModifier());
        simpleModule.setDeserializerModifier(new OverrideDeserializerModifier());
        builder.modules(simpleModule);
        return builder;
    }
}

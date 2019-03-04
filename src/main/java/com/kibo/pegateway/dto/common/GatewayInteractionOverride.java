package com.kibo.pegateway.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mozu.api.contracts.paymentservice.extensibility.v1.GatewayInteraction;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class GatewayInteractionOverride extends GatewayInteraction {
    @Getter
    @Setter
    @JsonSerialize(using = KeyValueTupleSerializer.class)
    @JsonDeserialize(using = KeyValueTupleDeserializer.class)
    @JsonProperty("configuration")
    Map<String, Object> configurationMap;
}

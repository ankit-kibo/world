package com.kibo.pegateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kibo.pegateway.dto.base.IGatewayRequest;
import com.kibo.pegateway.dto.common.KeyValueTupleDeserializer;
import com.kibo.pegateway.dto.common.KeyValueTupleSerializer;
import com.mozu.api.contracts.paymentservice.extensibility.v1.GatewayAuthorizationRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

public class GatewayAuthorizationRequestOverride extends GatewayAuthorizationRequest implements IGatewayRequest, Serializable {
    @Getter
    @Setter
    @JsonSerialize(using = KeyValueTupleSerializer.class)
    @JsonDeserialize(using = KeyValueTupleDeserializer.class)
    @JsonProperty("additionalData")
    Map<String, Object> additionalDataMap;
}

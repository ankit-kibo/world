package com.kibo.pegateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kibo.pegateway.dto.base.IGatewayAuthorizeResponse;
import com.kibo.pegateway.dto.common.KeyValueTupleDeserializer;
import com.kibo.pegateway.dto.common.KeyValueTupleSerializer;
import com.mozu.api.contracts.paymentservice.extensibility.v1.GatewayCaptureResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

public class GatewayCaptureResponseOverride extends GatewayCaptureResponse implements IGatewayAuthorizeResponse, Serializable {
    @Getter
    @Setter
    @JsonSerialize(using = KeyValueTupleSerializer.class)
    @JsonDeserialize(using = KeyValueTupleDeserializer.class)
    @JsonProperty("responseData")
    Map<String, Object> responseDataMap;
}

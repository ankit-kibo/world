package com.kibo.pegateway.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kibo.pegateway.dto.base.IHasMap;
import com.mozu.api.contracts.paymentservice.extensibility.v1.AdapterContext;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class AdapterContextOverride extends AdapterContext implements IHasMap {
    @Getter
    @Setter
    @JsonSerialize(using = KeyValueTupleSerializer.class)
    @JsonDeserialize(using = KeyValueTupleDeserializer.class)
    @JsonProperty("settings")
    Map<String, Object> settingsMap;
    @Getter
    @Setter
    @JsonSerialize(using = KeyValueTupleSerializer.class)
    @JsonDeserialize(using = KeyValueTupleDeserializer.class)
    @JsonProperty("configuration")
    Map<String, Object> configurationMap;
    @Getter
    @Setter
    @JsonIgnore
    transient String testingUrl;
    @Getter
    @Setter
    @JsonIgnore
    transient String productionUrl;

    @Override
    public void setPropertiesFromMap() throws Exception {
        if(configurationMap == null)
            configurationMap = new HashMap<>();
        testingUrl = IHasMap.getStringFromMap(configurationMap, "testingUrl",true, "configuration");
        productionUrl = IHasMap.getStringFromMap(configurationMap, "productionUrl",true, "configuration");
    }

    @Override
    public void addPropertiesToMap() {
        if(configurationMap == null)
            configurationMap = new HashMap<>();
        IHasMap.addStringToMap("testingUrl", testingUrl, configurationMap);
        IHasMap.addStringToMap("productionUrl", productionUrl, configurationMap);
    }
}

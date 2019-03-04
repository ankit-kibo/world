/*package com.kibo.pegateway.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kibo.pegateway.config.JacksonConfig;
import com.kibo.pegateway.dto.common.AdapterContextOverride;
import com.kibo.pegateway.dto.request.GatewayAuthorizationRequestOverride;
import com.mozu.api.contracts.paymentservice.extensibility.v1.AdapterContext;
import com.mozu.api.contracts.paymentservice.extensibility.v1.GatewayAuthorizationRequest;
import com.mozu.api.contracts.paymentservice.extensibility.v1.KeyValueTuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = "com.kibo")
@SpringBootTest
@Import({JacksonConfig.class, TestBeanProvider.class})
@Component
public class TestAuthorizeOverride {
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testAuthorizeOverride() throws IOException {
        GatewayAuthorizationRequest request = new GatewayAuthorizationRequest();
        request.setAmount((double) 123);
        request.setApiVersion("123");
        List<KeyValueTuple> additionalData = new ArrayList<>();
        KeyValueTuple kvt = new KeyValueTuple();
        kvt.setKey("test");
        kvt.setValue("testing");
        additionalData.add(kvt);
        //request.setAdditionalData(additionalData.toArray(new KeyValueTuple[0]));
        AdapterContext adapterContext = new AdapterContext();
        List<KeyValueTuple> configuration = new ArrayList<>();
        kvt = new KeyValueTuple();
        kvt.setKey("testingUrl");
        kvt.setValue("asdf");
        configuration.add(kvt);
        kvt = new KeyValueTuple();
        kvt.setKey("productionUrl");
        kvt.setValue("asdf2");
        configuration.add(kvt);
        adapterContext.setConfiguration(configuration);
        request.setContext(adapterContext);
        String jsonString = objectMapper.writeValueAsString(request);
        GatewayAuthorizationRequestOverride test =
                (GatewayAuthorizationRequestOverride) objectMapper.readValue(jsonString, GatewayAuthorizationRequest.class);
      //  assert(test.getAdditionalDataMap().get("test").equals("testing"));
      //  assert(((AdapterContextOverride)test.getContext()).getTestingUrl().equals("asdf"));
      //  assert(((AdapterContextOverride)test.getContext()).getProductionUrl().equals("asdf2"));
        jsonString = objectMapper.writeValueAsString(test);
        test = (GatewayAuthorizationRequestOverride) objectMapper.readValue(jsonString, GatewayAuthorizationRequest.class);
       // assert(test.getAdditionalDataMap().get("test").equals("testing"));
      //  assert(((AdapterContextOverride)test.getContext()).getTestingUrl().equals("asdf"));
       // assert(((AdapterContextOverride)test.getContext()).getProductionUrl().equals("asdf2"));
    }
}
*/
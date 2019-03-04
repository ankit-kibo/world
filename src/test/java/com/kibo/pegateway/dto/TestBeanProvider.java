package com.kibo.pegateway.dto;

import com.kibo.pegateway.IPeService;
import com.mozu.api.contracts.paymentservice.extensibility.v1.*;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class TestBeanProvider {
    @Bean
    public BuildProperties getBuildProperties() {
        Properties properties = new Properties();
        BuildProperties buildProperties = new BuildProperties(properties);
        return buildProperties;
    }

    @Bean
    public IPeService getPeService() {
        return new IPeService() {
            @Override
            public GatewayAuthorizeResponse authorize(GatewayAuthorizationRequest request) throws Exception {
                throw new Exception("Not implemented.");
            }

            @Override
            public GatewayCaptureResponse capture(CaptureRequest request) throws Exception {
                throw new Exception("Not implemented.");
            }

            @Override
            public GatewayCreditResponse credit(CaptureRequest request) throws Exception {
                throw new Exception("Not implemented.");
            }

            @Override
            public GatewayVoidResponse doVoid(CaptureRequest request) throws Exception {
                throw new Exception("Not implemented.");
            }

            @Override
            public GatewayCaptureResponse authorizeAndCapture(CaptureRequest request) throws Exception {
                throw new Exception("Not implemented.");
            }
        };
    }
}

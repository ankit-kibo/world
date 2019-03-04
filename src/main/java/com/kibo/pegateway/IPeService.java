package com.kibo.pegateway;

import com.mozu.api.contracts.paymentservice.extensibility.v1.*;

/**
 * Implement this class to implement the service.
 */
public interface IPeService {
    public GatewayAuthorizeResponse authorize(GatewayAuthorizationRequest request) throws Exception;
    public GatewayCaptureResponse capture(CaptureRequest request) throws Exception;
    public GatewayCreditResponse credit(CaptureRequest request) throws Exception;
    public GatewayVoidResponse doVoid(CaptureRequest request) throws Exception;
    public GatewayCaptureResponse authorizeAndCapture(CaptureRequest request) throws Exception;
}

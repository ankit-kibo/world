package com.kibo.pegateway.dto.base;

import com.mozu.api.contracts.paymentservice.extensibility.v1.ConnectionStatuses;

import java.util.Map;

/**
 * This allows the service to use transaction responses,
 * ie, non-authorization responses, in a generic fashion.
 */
public interface IGatewayTransactionResponse {
    public void setIsDeclined(Boolean isDeclined);

    public Boolean getIsDeclined();

    public void setResponseCode(String responseCode);

    public String getResponseCode();

    public void setResponseText(String responseText);

    public String getResponseText();

    public void setTransactionId(String transactionId);

    public String getTransactionId();

    public void setRemoteConnectionStatus(ConnectionStatuses remoteConnectionstatus);

    public ConnectionStatuses getRemoteConnectionStatus();

    //public void setResponseDataMap(Map<String, Object> responseDataMap);

   // public Map<String, Object> getResponseDataMap();
}

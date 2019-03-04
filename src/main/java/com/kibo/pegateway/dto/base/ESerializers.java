package com.kibo.pegateway.dto.base;

import com.kibo.pegateway.dto.common.AdapterContextOverride;
import com.kibo.pegateway.dto.request.CaptureRequestOverride;
import com.kibo.pegateway.dto.request.GatewayAuthorizationRequestOverride;
import com.kibo.pegateway.dto.response.GatewayAuthorizeResponseOverride;
import com.kibo.pegateway.dto.response.GatewayCaptureResponseOverride;
import com.kibo.pegateway.dto.response.GatewayCreditResponseOverride;
import com.kibo.pegateway.dto.response.GatewayVoidResponseOverride;
import com.mozu.api.contracts.paymentservice.extensibility.v1.*;

/**
 * Contains information about the serializers.
 *
 * Used to determine which class to use to
 * replace the given class.
 *
 * To override the values, call 'overrideSerializer'
 * in your main class before initializing Spring
 * so this information is known before Jackson starts.
 *
 * 'targetClass' contains the class in the contract
 * that is to be overridden.
 *
 * 'resultClass' contains the class to override it with.
 */
public enum ESerializers {
    AdapterContextOverrideSerializer(AdapterContext.class, AdapterContextOverride.class),
    CaptureRequestOverrideSerializer(CaptureRequest.class, CaptureRequestOverride.class),
    GatewayAuthorizationRequestOverrideSerializer(GatewayAuthorizationRequest.class, GatewayAuthorizationRequestOverride.class),
    GatewayAuthorizeResponseOverrideSerializer(GatewayAuthorizeResponse.class, GatewayAuthorizeResponseOverride.class),
    GatewayCaptureResponseOverrideSerializer(GatewayCaptureResponse.class, GatewayCaptureResponseOverride.class),
    GatewayCreditResponseOverrideSerializer(GatewayCreditResponse.class, GatewayCreditResponseOverride.class),
    GatewayVoidResponseOverrideSerializer(GatewayVoidResponse.class, GatewayVoidResponseOverride.class),
    DefaultSerializer(null, null);

    Class targetClass;
    Class resultClass;

    private ESerializers(Class targetClass, Class resultClass) {
        this.targetClass = targetClass;
        this.resultClass = resultClass;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Class getResultClass() {
        return resultClass;
    }

    /**
     * Overrides the resultClass for a given targetClass.
     *
     * Call this to override the behavior before Spring starts,
     * for instance, in your main class.
     * @param targetClass The class to override.
     * @param resultClass The class with which to override it.
     * @throws Exception Thrown if the class to override is not found.
     */
    public static void overrideSerializer(Class targetClass, Class resultClass) throws Exception {
        boolean found = false;
        for (ESerializers serializer : ESerializers.values()) {
            if(serializer == DefaultSerializer)
                break;
            if(serializer.targetClass == targetClass) {
                serializer.resultClass = resultClass;
                found = true;
                break;
            }
        }
        if(!found)
            throw new Exception("No serializer found for class '"+targetClass.getName()+"'.");
    }

    /**
     * Gets the ESerializers for a deserialize operation.
     * @param test The class to assign.
     * @return The serializer to use.
     */
    public static ESerializers getDeserializer(Class test) {
        if (test == null) {
            return null;
        }
        for (ESerializers serializer : ESerializers.values()) {
            if(serializer == DefaultSerializer)
                break;
            // If the result is the same as the request,
            // return the default to avoid recursively
            // calling the modified deserializer.
            if(serializer.resultClass == test)
                return DefaultSerializer;
            if (serializer.targetClass.isAssignableFrom(test)) {
                return serializer;
            }
        }
        return DefaultSerializer;
    }

    /**
     * Gets the ESerializers for a serialize operation.
     * @param test The class to assign.
     * @return The serializer to use.
     */
    public static ESerializers getSerializer(Class test) {
        if (test == null) {
            return null;
        }
        for (ESerializers serializer : ESerializers.values()) {
            if(serializer == DefaultSerializer)
                break;
            if (serializer.targetClass.isAssignableFrom(test)) {
                return serializer;
            }
        }
        return DefaultSerializer;
    }
}

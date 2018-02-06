package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ERIFResponse;

import static com.google.common.collect.Lists.newArrayList;

public class ERIFResponseData {

    public static ERIFResponse aERIFResponse(){
        return ERIFResponse.builder()
                .ban("BAN_123")
                .billingCycleDay(7)
                .childServiceProviderId("childServiceProviderId")
                .errDescription("errDesc")
                .errId("OK")
                .customerType("PRE")
                .serviceProviderId("serviceProviderId")
                .serviceProviderType("serviceProviderType")
                .status("ACCEPTED")
                .usergroups(newArrayList("test-ug1", "test-ug2"))
                .build();
    }
    public static ERIFResponse aERIFResponse(ChargingId chargingId, String status, String errorId, String errorDescription){
        return ERIFResponse.builder()
                .ban("BAN_" + chargingId.getValue())
                .billingCycleDay(7)
                .childServiceProviderId("childServiceProviderId")
                .errId(errorId)
                .errDescription(errorDescription)
                .customerType("PRE")
                .serviceProviderId("serviceProviderId")
                .serviceProviderType("serviceProviderType")
                .status(status)
                .usergroups(newArrayList("test-ug1", "test-ug2"))
                .build();
    }
}

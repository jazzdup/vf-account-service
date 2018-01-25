package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ERIFResponse;

import java.util.ArrayList;
import java.util.Arrays;

public class ERIFResponseData {
    public static ERIFResponse anERIFResponse(){
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
                .usergroups(new ArrayList<String>(Arrays.asList("ug1", "ug2")))
                .build();
    }
}

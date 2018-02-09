package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ERIFResponse;
import com.vodafone.charging.accountservice.domain.xml.Response;

import java.util.Calendar;

import static com.google.common.collect.Lists.newArrayList;

public class ResponseData {

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
    public static Response anXmlResponse(){
        Response response = new Response();
        response.setStatus("ACCEPTED");
        response.setErrId("OK");
        response.setBan("BAN_234");
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        response.setBillingCycleDay(dayOfMonth);
        Response.UserGroups userGroups = new Response.UserGroups();
        userGroups.getItem().add("ug1");
        userGroups.getItem().add("ug2");
        response.setUserGroups(userGroups);
        response.setSpId("MVNO");
        response.setIsPrepay("PRE");
        response.setErrDescription("errDescription");
        response.setChildSpId("MVNO_CHILD");
        response.setSpType("spType");
        return response;
    }
}

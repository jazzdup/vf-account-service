package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ERIFResponse;
import com.vodafone.charging.accountservice.dto.xml.Body;
import com.vodafone.charging.accountservice.dto.xml.Envelope;
import com.vodafone.charging.accountservice.dto.xml.Response;

import java.util.Calendar;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class IFResponseData {

    public static ERIFResponse aERIFResponse(){
        return ERIFResponse.builder()
                .ban("BAN_123")
                .billingCycleDay(9)
                .childSpId("childServiceProviderId")
                .errDescription("errDesc")
                .errId("OK")
                .isPrepay("PRE")
                .spId("serviceProviderId")
                .spType("serviceProviderType")
                .status("ACCEPTED")
                .userGroups(newArrayList("test-ug1", "test-ug2"))
                .build();
    }
    public static ERIFResponse aERIFResponse(ChargingId chargingId, String status, String errorId, String errorDescription){
        return ERIFResponse.builder()
                .ban("BAN_" + chargingId.getValue())
                .billingCycleDay(9)
                .childSpId("childServiceProviderId")
                .errDescription("errDesc")
                .errId("OK")
                .isPrepay("PRE")
                .spId("serviceProviderId")
                .spType("serviceProviderType")
                .status("ACCEPTED")
                .userGroups(newArrayList("test-ug1", "test-ug2"))
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
    public static Envelope anEnvelope(){
        Response.UserGroups ugs = new Response.UserGroups();
        List<String> items = ugs.getItem();
        items = newArrayList("test-ug1", "test-ug2");
        Response response = new Response();
        response.setBan("BAN_123");
        response.setBillingCycleDay(7);
        response.setChildSpId("childServiceProviderId");
        response.setErrDescription("errDesc");
        response.setErrId("OK");
        response.setIsPrepay("PRE");
        response.setSpId("serviceProviderId");
        response.setSpType("serviceProviderType");
        response.setStatus("ACCEPTED");
        response.setUserGroups(ugs);
        Body body = new Body();
        body.setResponse(response);
        Envelope envelope = new Envelope();
        envelope.setBody(body);
        return envelope;
    }
}

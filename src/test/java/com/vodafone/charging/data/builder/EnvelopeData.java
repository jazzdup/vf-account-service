package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.xml.*;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class EnvelopeData {
    public static Envelope buildEnvelope(ContextData contextData){
        Msgcontrol msgcontrol= new Msgcontrol();
        msgcontrol.setCountry(contextData.getLocale().getCountry());

        AccountId accountId = new AccountId();
        accountId.setType(contextData.getChargingId().getType());
        accountId.setValue(contextData.getChargingId().getValue());

        Validate validate = new Validate();
        validate.setAccountId(accountId);
        validate.setClientId(contextData.getClientId());
        validate.setKycCheck(contextData.isKycCheck());
        validate.setPackageType(contextData.getPackageType().name());
        validate.setPartnerId(contextData.getPartnerId());
        validate.setServiceId(contextData.getServiceId());
        validate.setVendorId(contextData.getVendorId());

        Request request = new Request();
        request.setMsgcontrol(msgcontrol);
        request.setValidate(validate);

        Messagegroup messagegroup = new Messagegroup();
        messagegroup.setRequest(request);

        Body body = new Body();
        body.setMessagegroup(messagegroup);
        Envelope envelope = new Envelope();
        envelope.setBody(body);

        return  envelope;
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

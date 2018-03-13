package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.dto.json.ERIFResponse;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.dto.xml.Response;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.accountservice.exception.ErrorIds.VAS_INTERNAL_SERVER_ERROR;
import static java.lang.String.valueOf;

/**
 * Represents Account information after interaction with an external system e.g. IF Handlers
 */
public class EnrichedAccountInfoDataBuilder {

    public static EnrichedAccountInfo aEnrichedAccountInfo() {

        Random random = new Random();

        return new EnrichedAccountInfo.Builder("OK")
                .usergroups(newArrayList(valueOf(random.nextInt()), valueOf(random.nextInt())))
                .ban(random.nextInt() + "_ban")
                .billingCycleDay(random.nextInt())
                .serviceProviderId("serviceProviderId")
                .childServiceProviderId("childServiceProviderId")
                .serviceProviderType("serviceProviderType")
                .customerType("PRE")
                .errorId("test-error-id")
                .errorDescription("test-error-description")
                .build();
    }

    public static EnrichedAccountInfo aEnrichedAccountInfo(ERIFResponse response) {
        return new EnrichedAccountInfo(response, null);
    }

    public static EnrichedAccountInfo aEnrichedAccountInfo(Response response) {
        return new EnrichedAccountInfo(response, null);
    }
//
//    public static EnrichedAccountInfo aEnrichedAccountInfo(ContextData contextData) {
//            validationStatus = erifResponse.getStatus();
//            ban = erifResponse.getBan();
//            Response.UserGroups userGroups = erifResponse.getUserGroups();
//            usergroups = new ArrayList<String>();
//            for (String item: userGroups.getItem()) {
//                usergroups.add(item);
//            }
//            billingCycleDay = erifResponse.getBillingCycleDay();
//            serviceProviderId = erifResponse.getSpId();
//            childServiceProviderId = erifResponse.getChildSpId();
//            serviceProviderType = erifResponse.getSpType();
//            serviceProviderId = erifResponse.getSpId();
//            customerType = erifResponse.getIsPrepay();
//            errorId = erifResponse.getErrId();
//            errorDescription = erifResponse.getErrDescription();
//        }
//        Envelope envelope = EnvelopeData.buildEnvelope(contextData);
//        Response response = envelope.getBody().getResponse();
//        return new EnrichedAccountInfo(response);
//    }

    /**
     * returns the same subset of fields as current ERIF test system (well, it's my ERIF modified)
     * @param chargingId
     * @return
     */
    public static EnrichedAccountInfo aEnrichedAccountInfoForTestERIFJson(ChargingId chargingId) {
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        return new EnrichedAccountInfo.Builder("ACCEPTED")
                .ban("BAN_" + chargingId.getValue())
                .errorId("OK")
                .errorDescription("errDescription")
                .billingCycleDay(dayOfMonth)
                .build();
    }
    /**
     * returns the same subset of fields as current ERIF test system (well, it's my ERIF modified)
     * @param chargingId
     * @return
     */
    public static EnrichedAccountInfo aEnrichedAccountInfoForTestERIFXml(ChargingId chargingId) {
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        return new EnrichedAccountInfo.Builder("ACCEPTED")
                .ban("BAN_" + chargingId.getValue())
                .errorId("OK")
                .errorDescription("errDescription")
                .billingCycleDay(dayOfMonth)
                .usergroups(Arrays.asList("ug2", "ug1"))
                .serviceProviderId("MVNO")
                .childServiceProviderId("MVNO_CHILD")
                .serviceProviderType("spType")
                .customerType("PRE")
                .build();
    }

    public static EnrichedAccountInfo aEnrichedAccountInfoWhen500Response() {
        return new EnrichedAccountInfo.Builder("ERROR")
                .errorId(VAS_INTERNAL_SERVER_ERROR.errorId())
                .errorDescription(VAS_INTERNAL_SERVER_ERROR.errorDescription()).build();

    }
}

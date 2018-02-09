package com.vodafone.charging.mock;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.enums.ResponseType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.vodafone.charging.mock.IFRequestNamespaceEnum.SOAP_NS;
import static com.vodafone.charging.mock.IFRequestNamespaceEnum.VODAFONE_NS;
import static com.vodafone.charging.mock.IFRequestXpathEnum.VALIDATE;
import static com.vodafone.charging.mock.IFSoapResponseDataBuilder.aAccountValidationSoapResponse;
import static wiremock.org.apache.http.HttpStatus.SC_OK;


//Prepares Wiremock for various ER IF responses.

// This file should only contain the "happy" case responses which allow for easy test creation.
// In most cases this should suffice.
// If you want to configure the response more finely, use the WiremockPreparer subclass
public class WiremockDefaultPreparer {

    static final String IF_TEST_URL = "/broker/router.jsp";
    public static final String TARGET = "x-vf-target";
    public static final String GLOBAL_HEADER_VALUE = "global";
    public static final String LOCAL_HEADER_VALUE = "local";

    public static void prepareForValidateJson(final ChargingId chargingId) {
        String body = "{\"status\":\"ACCEPTED\",\"ban\":\"BAN_123\",\"errId\":\"OK\",\"errDescription\":\"errDesc\"" +
                ",\"billingCycleDay\":9,\"spId\":\"serviceProviderId\",\"isPrepay\":\"PRE\"" +
                ",\"childSpId\":\"childServiceProviderId\",\"spType\":\"serviceProviderType\"" +
                ",\"userGroups\":[\"test-ug2\",\"test-ug1\"]}\n";
        stubFor(post(urlEqualTo(IF_TEST_URL))
                .withRequestBody(equalToJson("{\"messageControl\":{\"locale\":\"en_GB\"},\n" +
                        "\"routable\":{\"type\":\"validate\",\"chargingId\":{\"type\":\"msisdn\",\"value\":\"" + chargingId.getValue() + "\"},\"clientId\":\"clientId\",\"kycCheck\":false}}", true, true))
                        .willReturn(aResponse()
                                .withStatus(SC_OK)
                                .withHeader("connection", "keep-alive")
                                .withHeader("Content-Type", "application/json")
                                .withHeader("transfer-encoding", "chunked")
                                .withBody(body))
//                                .withBodyFile("erifResponseValidate.txt"))
        );
    }

    public static void prepareForValidateSoap(final ChargingId chargingId, String ban) {
        String validateResponse = aAccountValidationSoapResponse(ResponseType.OK, ban, "OK", null);
        stubFor(post(urlEqualTo(IF_TEST_URL))
                        .withRequestBody(matchingXPath(VALIDATE.path())
                                .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                                .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                        .willReturn(aResponse()
                                .withStatus(SC_OK)
                                .withHeader("connection", "keep-alive")
                                .withHeader("Content-Type", "application/xml")
                                .withHeader("transfer-encoding", "chunked")
                                .withBody(validateResponse))
        );
    }

}

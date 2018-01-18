package com.vodafone.charging.mock;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.enums.ResponseType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.vodafone.charging.accountservice.domain.enums.Headers.REQUEST_CHARGING_ID_HEADER_NAME;
import static com.vodafone.charging.mock.IFRequestNamespaceEnum.SOAP_NS;
import static com.vodafone.charging.mock.IFRequestNamespaceEnum.VODAFONE_NS;
import static com.vodafone.charging.mock.IFRequestXpathEnum.VALIDATE;
import static com.vodafone.charging.mock.IFSoapResponseDataBuilder.aAccountValidationSoapResponse;
import static wiremock.org.apache.http.HttpStatus.SC_OK;

//Helps to setup Wiremock so it responds with appropriate IF messages.
//If you want to check a specific xPath / change the response / check a failure scenario then use one of the overloaded methods in this file.
//The superclass has the default responses if you want just the happy case.
public class WiremockPreparer extends WiremockDefaultPreparer {

    public static StubMapping prepareForValidate(ChargingId chargingId,
									            String xPath,
									            String response) {
    	return stubFor(post(urlEqualTo(IF_TEST_URL))
    			.withHeader(REQUEST_CHARGING_ID_HEADER_NAME, equalTo(chargingId.toString()))
    			.withRequestBody(matchingXPath(xPath)
    					.withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
    					.withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
    			.withRequestBody(containing(chargingId.getValue()))
    			.willReturn(aResponse()
    					.withStatus(SC_OK)
    					.withBody(response))
    			);
    }
    
	/**
	 * JIRA ET-2057:KYC success would be verification_not_required|verified, KYC failed would be not-verified|in-progress|rejected;
	 */
    public static void prepareForValidateKYCError(final ChargingId chargingId, ResponseType responseType, String errorDesc) {
        String validateResponse = aAccountValidationSoapResponse(responseType, chargingId.getValue(), errorDesc, null);
        stubFor(post(urlEqualTo(IF_TEST_URL))
                        .withRequestBody(matchingXPath(VALIDATE.path())
                                .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                                .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                        .willReturn(aResponse()
                                .withStatus(SC_OK)
                                .withBody(validateResponse))
        );
    }
    
}

package com.vodafone.charging.mock;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.enums.Headers;
import com.vodafone.charging.accountservice.domain.enums.ResponseType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.vodafone.charging.mock.IFSoapResponseDataBuilder.*;
import static com.vodafone.charging.mock.IFRequestNamespaceEnum.SOAP_NS;
import static com.vodafone.charging.mock.IFRequestNamespaceEnum.VODAFONE_NS;
import static com.vodafone.charging.mock.IFRequestXpathEnum.*;
import static org.apache.http.HttpStatus.SC_OK;


//Prepares Wiremock for various ER IF responses.

// This file should only contain the "happy" case responses which allow for easy test creation.
// In most cases this should suffice.
// If you want to configure the response more finely, use the WiremockPreparer subclass
public class WiremockDefaultPreparer {

    static final String IF_TEST_URL = "/broker/router.jsp";
    public static final String TARGET = "x-vf-target";
    public static final String GLOBAL_HEADER_VALUE = "global";
    public static final String LOCAL_HEADER_VALUE = "local";
    
    //Typical purchase will require mocking for validation, paymentAuth, paymentCapture, notification
    public static void prepareForSuccessfulPurchase(final ChargingId chargingId) {

        prepareForValidate(chargingId);
        prepareForPaymentAuth(chargingId);
        prepareForCapture(chargingId);
        prepareForNotification(chargingId);
    }

    public static void prepareForSuccessfulPurchaseAndProvision(final ChargingId chargingId) {
        prepareForSuccessfulPurchase(chargingId);
        WiremockPreparer.prepareForProvision(chargingId);

    }

    public static void prepareForValidate(final ChargingId chargingId) {
        String validateResponse = aAccountValidationSoapResponse(ResponseType.OK, chargingId.getValue(), "OK", null);
        stubFor(post(urlEqualTo(IF_TEST_URL))
                        .withRequestBody(matchingXPath(VALIDATE.path())
                                .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                                .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                        .willReturn(aResponse()
                                .withStatus(SC_OK)
                                .withBody(validateResponse))
        );
    }

    public static void prepareForPaymentAuth(final ChargingId chargingId) {
        String paymentAuthResponse = aPaymentAuthSoapResponse(ResponseType.OK, chargingId.getValue(), "OK", null);
        stubFor(post(urlEqualTo(IF_TEST_URL))
                        .withRequestBody(matchingXPath(PAYMENT_AUTH.path())
                                .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                                .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                        .willReturn(aResponse()
                                .withStatus(SC_OK)
                                .withBody(paymentAuthResponse))
        );
    }

    public static void prepareForCapture(final ChargingId chargingId) {

        stubFor(post(urlEqualTo(IF_TEST_URL))
                .withHeader(Headers.REQUEST_CHARGING_ID_HEADER_NAME, equalTo(chargingId.toString()))
                .withRequestBody(matchingXPath(PAYMENT_CAPTURE.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(aPaymentCaptureSoapResponse(ResponseType.OK)))
        );
    }


    public static void prepareForNotification(final ChargingId chargingId) {
        String notificationResponse = aNotificationSoapResponse(ResponseType.OK);
        stubFor(post(urlEqualTo(IF_TEST_URL))
                .withHeader(Headers.REQUEST_CHARGING_ID_HEADER_NAME, equalTo(chargingId.toString()))
                .withRequestBody(matchingXPath(NOTIFICATION.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(notificationResponse))
        );
    }

    public static void prepareForProvision(final ChargingId chargingId) {
        String aProvisionSoapResponse = aProvisionSoapResponse(ResponseType.OK);

        stubFor(post(urlEqualTo(IF_TEST_URL))
//                .inScenario(getSubStatusString(BEING_PROVISIONED))
//                .whenScenarioStateIs(STARTED)
                .withHeader(Headers.REQUEST_CHARGING_ID_HEADER_NAME, equalTo(chargingId.toString()))
                .withRequestBody(matchingXPath(PROVISION.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
//                .willSetStateTo(getSubStatusString(ACTIVE))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(aProvisionSoapResponse))
        );

    }

//    public static void prepareForProvisionUpdate(ChargingId chargingId, String provisionId) {
//        String aProvisionSoapCallback = aProvisionSoapCallback(provisionId);
//        //provision callback
//        stubFor(post(urlEqualTo(IF_TEST_URL))
//                .inScenario(getSubStatusString(BEING_PROVISIONED))
//                .whenScenarioStateIs(getSubStatusString(ACTIVE))
//                .withHeader(Headers.REQUEST_CHARGING_ID_HEADER_NAME, equalTo(chargingId.toString()))
//                .withRequestBody(matchingXPath(PROVISION.path())
//                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
//                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
//                .willReturn(aResponse()
//                        .withStatus(SC_OK)
//                        .withBody(aProvisionSoapCallback))
//        );
//    }


    public static void prepareForRefund(ChargingId chargingId) {
        String refundResponse = aSoapRefundResponse(ResponseType.OK, chargingId.getValue(), "OK");
        stubFor(post(urlEqualTo(IF_TEST_URL))
                .withRequestBody(matchingXPath(PAYMENT_REFUND.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(refundResponse))
        );
    }

    public static void prepareForCancel(ChargingId chargingId) {
        String cancelResponse = aSoapCancelResponse(ResponseType.OK, chargingId.getValue(), "OK");
        stubFor(post(urlEqualTo(IF_TEST_URL))
                .withRequestBody(matchingXPath(PAYMENT_CANCEL.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(cancelResponse))
        );
    }
    
    public static void prepareForValidateLocalHeader(ChargingId chargingId) {
        String validateResponse = aAccountValidationSoapResponse(ResponseType.OK, chargingId.getValue(), "OK", null);
        stubFor(post(urlEqualTo(IF_TEST_URL))
        				.withHeader(TARGET, equalTo(LOCAL_HEADER_VALUE))
                        .withRequestBody(matchingXPath(VALIDATE.path())
                                .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                                .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                        .willReturn(aResponse()
                                .withStatus(SC_OK)
                                .withBody(validateResponse))
        );
    }
    
    public static void prepareForGlobalHeader(ChargingId chargingId) {
    	String response = aSoapPaymentResponse(ResponseType.OK, "BAN_" + chargingId.getValue(), "0001" + chargingId.getValue(), "OK");
		stubFor(post(urlEqualTo(IF_TEST_URL))
	           .withHeader(TARGET, equalTo(GLOBAL_HEADER_VALUE))
	           .willReturn(aResponse()
	                   .withStatus(SC_OK)
	                   .withBody(response))
	    );
    }
    
    public static void prepareForPaymentAuthGlobalHeader(ChargingId chargingId) {
    	String paymentAuthResponse = aPaymentAuthSoapResponse(ResponseType.OK, chargingId.getValue(), "OK", null);
        stubFor(post(urlEqualTo(IF_TEST_URL))
        				.withHeader(TARGET, equalTo(GLOBAL_HEADER_VALUE))
                        .withRequestBody(matchingXPath(PAYMENT_AUTH.path())
                                .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                                .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                        .willReturn(aResponse()
                                .withStatus(SC_OK)
                                .withBody(paymentAuthResponse))
        );
    }


    public static void prepareForSuccessfulPurchaseMsisdn(final String msisdn) {

        String paymentCaptureResponse = aPaymentCaptureSoapResponse(ResponseType.OK);

        prepareForValidateMsisdn(msisdn);
        prepareForPaymentAuthMsisdn(msisdn);

        stubFor(post(urlEqualTo(IF_TEST_URL))
                .withRequestBody(matchingXPath(PAYMENT_CAPTURE_AND_NOTIFICATION.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(paymentCaptureResponse))
        );
    }

    public static void prepareForSuccessfulPurchaseAndProvisionMsisdn(final String msisdn) {
        prepareForSuccessfulPurchaseMsisdn(msisdn);
        prepareForProvisionMsisdn(msisdn);
    }

    public static void prepareForValidateMsisdn(final String msisdn) {
        String validateResponse = aAccountValidationSoapResponse(ResponseType.OK, msisdn, "OK", null);
        stubFor(post(urlEqualTo(IF_TEST_URL))
                .withRequestBody(matchingXPath(VALIDATE.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(validateResponse))
        );
    }

    public static void prepareForPaymentAuthMsisdn(final String msisdn) {
        String paymentAuthResponse = aPaymentAuthSoapResponse(ResponseType.OK, msisdn, "OK", null);
        stubFor(post(urlEqualTo(IF_TEST_URL))
                .withRequestBody(matchingXPath(PAYMENT_AUTH.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(paymentAuthResponse))
        );
    }

    public static void prepareForProvisionMsisdn(final String msisdn) {
        String aProvisionSoapResponse = aProvisionSoapResponse(ResponseType.OK);

        stubFor(post(urlEqualTo(IF_TEST_URL))
                        .withRequestBody(matchingXPath(PROVISION.path())
                                .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                                .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                        .willReturn(aResponse()
                                .withStatus(SC_OK)
                                .withBody(aProvisionSoapResponse))
        );

    }

    public static void prepareForCaptureGlobalHeader(ChargingId chargingId) {
        stubFor(post(urlEqualTo(IF_TEST_URL))
        		.withHeader(TARGET, equalTo(GLOBAL_HEADER_VALUE))
                .withRequestBody(matchingXPath(IFRequestXpathEnum.PAYMENT_CAPTURE.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(aPaymentCaptureSoapResponse(ResponseType.OK)))
        );
    }
    
    public static void prepareForCancelGlobalHeader(ChargingId chargingId) {
        String cancelResponse = aSoapCancelResponse(ResponseType.OK, chargingId.getValue(), "OK");
        stubFor(post(urlEqualTo(IF_TEST_URL))
        		.withHeader(TARGET, equalTo(GLOBAL_HEADER_VALUE))
        		.withRequestBody(matchingXPath(PAYMENT_CANCEL.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(cancelResponse))
        );
    }
    
    public static void prepareForNotificationGlobalHeader(ChargingId chargingId) {
        String notificationResponse = aNotificationSoapResponse(ResponseType.OK);
        stubFor(post(urlEqualTo(IF_TEST_URL))
        		.withHeader(TARGET, equalTo(GLOBAL_HEADER_VALUE))
                .withHeader(Headers.REQUEST_CHARGING_ID_HEADER_NAME, equalTo(chargingId.toString()))
                .withRequestBody(matchingXPath(IFRequestXpathEnum.NOTIFICATION.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(notificationResponse))
        );
    }
    
    public static void prepareForRefundGlobalHeader(ChargingId chargingId) {
        String refundResponse = aSoapRefundResponse(ResponseType.OK, chargingId.getValue(), "OK");
        stubFor(post(urlEqualTo(IF_TEST_URL))
        		.withHeader(TARGET, equalTo(GLOBAL_HEADER_VALUE))
                .withRequestBody(matchingXPath(PAYMENT_REFUND.path())
                        .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                        .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                .willReturn(aResponse()
                        .withStatus(SC_OK)
                        .withBody(refundResponse))
        );
    }
    
    public static void prepareForPaymentAuth(final ChargingId chargingId, ResponseType responseType, String errorDesc) {
        String paymentAuthResponse = aPaymentAuthSoapResponse(responseType, chargingId.getValue(), errorDesc, null);
        stubFor(post(urlEqualTo(IF_TEST_URL))
                        .withRequestBody(matchingXPath(PAYMENT_AUTH.path())
                                .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
                                .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
                        .willReturn(aResponse()
                                .withStatus(SC_OK)
                                .withBody(paymentAuthResponse))
        );
    }

	public static StubMapping prepareForValidate(ChargingId chargingId, String prepay) {
		String validateResponsePrepay = aAccountValidationSoapResponse(ResponseType.OK, chargingId.getValue(), "OK", prepay);
	    return stubFor(post(urlEqualTo(IF_TEST_URL))
	    				.withHeader(Headers.REQUEST_CHARGING_ID_HEADER_NAME, equalTo(chargingId.toString()))
	                    .withRequestBody(matchingXPath(VALIDATE.path())
	                            .withXPathNamespace(SOAP_NS.prefix(), SOAP_NS.url())
	                            .withXPathNamespace(VODAFONE_NS.prefix(), VODAFONE_NS.url()))
	                    .willReturn(aResponse()
	                            .withStatus(SC_OK)
	                            .withBody(validateResponsePrepay))
	    );
	}
	
	public static void prepareMockPaymentAuthRejected(ChargingId chargingId) {
    	
    	prepareForValidate(chargingId);
        prepareForPaymentAuth(chargingId, ResponseType.REJECTED_OTHER, "User temp barred");
        prepareForCapture(chargingId);
        prepareForNotification(chargingId);
    }
	
   public static void prepareMockPaymentAuthDenied(ChargingId chargingId) {
    	
    	prepareForValidate(chargingId);
    	prepareForPaymentAuth(chargingId, ResponseType.DENIED_OTHER, "No longer with VF");
        prepareForCapture(chargingId);
        prepareForNotification(chargingId);
    }
   
   public static void prepareMockPaymentAuthError(ChargingId chargingId) {
   	
   	prepareForValidate(chargingId);
   	prepareForPaymentAuth(chargingId, ResponseType.SYSTEM_ERROR, "System error occured");
       prepareForCapture(chargingId);
       prepareForNotification(chargingId);
   }
}

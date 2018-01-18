package com.vodafone.charging.mock;

import com.vodafone.charging.accountservice.domain.enums.ResponseType;
import org.apache.commons.lang.StringUtils;

//Possible SOAP responses we can get from the IF.
//Meant for Component Integration tests only

//If this starts to grow we should create a test client to create messages programmatically.
//Currently IF only has a few different types of request types, so this will suffice currently.

public class IFSoapResponseDataBuilder {

    public static String aAccountValidationSoapResponse(ResponseType responseType, String ban, String errorDesc, String prepay) {
    	String pp = StringUtils.isNotBlank(prepay)? "   <v:isPrepay>" + prepay + "</v:isPrepay>\n" : "";
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
                "   <v:ban>" + ban + "</v:ban>\n" +
                "   <v:errId>" + responseType + "</v:errId>\n" +
                "   <v:errDescription>" + errorDesc + "</v:errDescription>\n" +
                "   <v:billingCycleDay>8</v:billingCycleDay>\n" +
                pp + 
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }


//    public static String aAccountValidationJsonResponse(){
//        return "{
//                "status": "ACCEPTED",
//                "ban": "BAN_7777",
//                "errId": "OK",
//                "billingCycleDay": 18
//                }""
//
//
//    }


    public static String aValidateSoapResponse() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>ACCEPTED</v:status>\n" +
                "   <v:ban>BAN_P149848933355842</v:ban>\n" +
                "   <v:errId>OK</v:errId>\n" +
                "   <v:billingCycleDay>26</v:billingCycleDay>\n" +
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }

    public static String aPaymentAuthSoapResponse(ResponseType responseType, String authCode, String errorDesc, String remainingBalance) {
    	String rb = StringUtils.isNotBlank(remainingBalance) ? "<v:remainingBalance>" + remainingBalance + "</v:remainingBalance>\n" : "";
    	return "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
                "   <v:authcode>" + authCode + "</v:authcode>\n" +
                "   <v:errId>" + responseType + "</v:errId>\n" +
                "   <v:paymentInfo>This is payment info from Pay Auth handler</v:paymentInfo>\n" +
                "   <v:billingCycleDay>0</v:billingCycleDay>\n" +
                rb +
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }

    public static String aPaymentCaptureSoapResponse(ResponseType responseType) {
        return "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
                "   <v:errId>" + responseType + "</v:errId>\n" +
                "   <v:billingCycleDay>0</v:billingCycleDay>\n" +
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }

    public static String aNotificationSoapResponse(ResponseType responseType) {
        return "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
                "   <v:errId>" + responseType + "</v:errId>\n" +
                "   <v:billingCycleDay>0</v:billingCycleDay>\n" +
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }

    //TODO - Check this is the correct type of response
    public static String aProvisionSoapResponse(ResponseType responseType) {
        return "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
                "   <v:errId>" + responseType + "</v:errId>\n" +
                "   <v:billingCycleDay>0</v:billingCycleDay>\n" +
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }

    public static String aProvisionSoapCallback(String provisioningId) {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<er-request id=\"100048\" client-application-id=\"erif\" purchase_locale=\"en_GB\" language_locale=\"en_GB\" client-domain=\"er-if\">\n" +
                "  <payload>\n" +
                "    <provision-full-update-service-status-request>\n" +
                "      <provisioning-id>"+ provisioningId + "</provisioning-id>\n" +
                "      <service-status>2</service-status>\n" +
                "      <provisioning-status>221</provisioning-status>\n" +
                "    </provision-full-update-service-status-request>\n" +
                "  </payload>\n" +
                "</er-request>";
    }


    public static String aSoapPaymentResponse(ResponseType responseType, String ban, String authcode, String errorDesc) {

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
                "   <v:authcode>" + authcode + "</v:authcode>\n" +
                "   <v:ban>" + ban + "</v:ban>\n" +
                "   <v:errId>" + responseType + "</v:errId>\n" +
                "   <v:errDescription>" + errorDesc + "</v:errDescription>\n" +
                "   <v:billingCycleDay>8</v:billingCycleDay>\n" +
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }

    public static String aSoapRefundResponse(ResponseType responseType, String authCode, String errorDesc) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
                "   <v:authcode>" + authCode + "</v:authcode>\n" +
                "   <v:errId>" + responseType + "</v:errId>\n" +
                "   <v:errDescription>" + errorDesc + "</v:errDescription>\n" +
                "   <v:billingCycleDay>0</v:billingCycleDay>\n" +
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }

    public static String aSoapCancelResponse(ResponseType responseType, String authCode, String errorDesc) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
                "   <v:authcode>" + authCode + "</v:authcode>\n" +
                "   <v:errId>" + responseType + "</v:errId>\n" +
                "   <v:errDescription>" + errorDesc + "</v:errDescription>\n" +
                "   <v:billingCycleDay>0</v:billingCycleDay>\n" +
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }

//    public static String aIOTPaymentAuthSoapResponse(ResponseType responseType,
//                                                     String authCode,
//                                                     String errorCode,
//                                                     IOTExternalPaymentDetails paymentDetails) {
//        return "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
//                " <SOAP-ENV:Body>\n" +
//                "  <Response>\n" +
//                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
//                "   <v:authcode>" + authCode + "</v:authcode>\n" +
//                "   <v:errId>" + responseType + "</v:errId>\n" +
//                "   <v:paymentInfo>This is payment info from Pay Auth handler</v:paymentInfo>\n" +
//                "   <v:billingCycleDay>0</v:billingCycleDay>\n" +
//                "   <v:extPaymentProviderRefId>" + paymentDetails.getExtPaymentProviderRefId() + "</v:extPaymentProviderRefId>\n" +
//                "   <v:extCardLastDigits>" + paymentDetails.getExtCardLastDigits() + "</v:extCardLastDigits>\n" +
//                "   <v:extCardType>" + paymentDetails.getExtCardType() + "</v:extCardType>\n" +
//                "   <v:extCardExpiryMonth>" + paymentDetails.getExtCardExpiryMonth() + "</v:extCardExpiryMonth>\n" +
//                "   <v:extCardExpiryYear>" + paymentDetails.getExtCardExpiryYear() + "</v:extCardExpiryYear>\n" +
//                "  </Response>\n" +
//                " </SOAP-ENV:Body>\n" +
//                "</SOAP-ENV:Envelope>";
//    }
    
    //ET1817
    public static String aRemainingBalanceSoapResponse(ResponseType responseType, Double remainingBalance, String errorDesc) {
    	return "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
                "   <v:errId>" + responseType + "</v:errId>\n" +
                "   <v:errDescription>" + errorDesc + "</v:errDescription>\n" +                
                "   <v:remainingBalance>" + remainingBalance + "</v:remainingBalance>\n" +
                "   <v:billingCycleDay>0</v:billingCycleDay>\n" +                
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }
}

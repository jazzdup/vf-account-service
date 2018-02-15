package com.vodafone.charging.mock;

import com.vodafone.charging.accountservice.domain.enums.ResponseType;
import com.vodafone.charging.accountservice.dto.xml.Response;
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
    public static String aAccountValidationSoapResponse(Response response, ResponseType responseType) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<SOAP-ENV:Envelope xmlns:v=\"http://www.vizzavi.net/chargingandpayments/message/1.0\" xmlns:SOAP-ENV=\"http://www.w3.org/2001/12/soap-envelope\">\n" +
                " <SOAP-ENV:Body>\n" +
                "  <Response>\n" +
                "   <v:status>" + responseType.getStatus() + "</v:status>\n" +
                "   <v:ban>" + response.getBan() + "</v:ban>\n" +
                "   <v:errId>" + responseType + "</v:errId>\n" +
                "   <v:errDescription>" + response.getErrDescription()+ "</v:errDescription>\n" +
                "   <v:userGroups>\n" +
                "    <v:item>" + response.getUserGroups().getItem().get(0) + "</v:item>\n" +
                "    <v:item>" + response.getUserGroups().getItem().get(1) + "</v:item>\n" +
                "   </v:userGroups>\n" +
                "   <v:billingCycleDay>" + response.getBillingCycleDay() + "</v:billingCycleDay>\n" +
                "   <v:spId>" + response.getSpId() + "</v:spId>" +
                "   <v:isPrepay>" + response.getIsPrepay() + "</v:isPrepay>\n" +
                "   <v:childSpId>" + response.getChildSpId() + "</v:childSpId>\n" +
                "   <v:spType>" + response.getSpType() + "</v:spType>" +
                "  </Response>\n" +
                " </SOAP-ENV:Body>\n" +
                "</SOAP-ENV:Envelope>";
    }



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

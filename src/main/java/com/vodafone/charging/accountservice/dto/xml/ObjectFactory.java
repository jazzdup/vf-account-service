//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.06 at 06:47:13 PM GMT 
//


package com.vodafone.charging.accountservice.dto.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.vizzavi.chargingandpayments.message._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Country_QNAME = new QName("http://www.vizzavi.net/chargingandpayments/message/1.0", "country");
    private final static QName _ClientId_QNAME = new QName("http://www.vizzavi.net/chargingandpayments/message/1.0", "clientId");
    private final static QName _KycCheck_QNAME = new QName("http://www.vizzavi.net/chargingandpayments/message/1.0", "kycCheck");
    private final static QName _ServiceId_QNAME = new QName("http://www.vizzavi.net/chargingandpayments/message/1.0", "serviceId");
    private final static QName _PartnerId_QNAME = new QName("http://www.vizzavi.net/chargingandpayments/message/1.0", "partnerId");
    private final static QName _PackageType_QNAME = new QName("http://www.vizzavi.net/chargingandpayments/message/1.0", "packageType");
    private final static QName _VendorId_QNAME = new QName("http://www.vizzavi.net/chargingandpayments/message/1.0", "vendorId");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.vizzavi.chargingandpayments.message._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Response }
     * 
     */
    public Response createResponse() {
        return new Response();
    }

    /**
     * Create an instance of {@link AccountId }
     * 
     */
    public AccountId createAccountId() {
        return new AccountId();
    }

    /**
     * Create an instance of {@link Msgcontrol }
     * 
     */
    public Msgcontrol createMsgcontrol() {
        return new Msgcontrol();
    }

    /**
     * Create an instance of {@link Validate }
     * 
     */
    public Validate createValidate() {
        return new Validate();
    }

    /**
     * Create an instance of {@link Request }
     * 
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link Messagegroup }
     * 
     */
    public Messagegroup createMessagegroup() {
        return new Messagegroup();
    }

    /**
     * Create an instance of {@link Response.UserGroups }
     * 
     */
    public Response.UserGroups createResponseUserGroups() {
        return new Response.UserGroups();
    }

    /**
     * Create an instance of {@link Body }
     * 
     */
    public Body createBody() {
        return new Body();
    }

    /**
     * Create an instance of {@link Envelope }
     * 
     */
    public Envelope createEnvelope() {
        return new Envelope();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vizzavi.net/chargingandpayments/message/1.0", name = "country")
    public JAXBElement<String> createCountry(String value) {
        return new JAXBElement<String>(_Country_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vizzavi.net/chargingandpayments/message/1.0", name = "clientId")
    public JAXBElement<String> createClientId(String value) {
        return new JAXBElement<String>(_ClientId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vizzavi.net/chargingandpayments/message/1.0", name = "kycCheck")
    public JAXBElement<String> createKycCheck(String value) {
        return new JAXBElement<String>(_KycCheck_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vizzavi.net/chargingandpayments/message/1.0", name = "serviceId")
    public JAXBElement<String> createServiceId(String value) {
        return new JAXBElement<String>(_ServiceId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vizzavi.net/chargingandpayments/message/1.0", name = "partnerId")
    public JAXBElement<String> createPartnerId(String value) {
        return new JAXBElement<String>(_PartnerId_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vizzavi.net/chargingandpayments/message/1.0", name = "packageType")
    public JAXBElement<String> createPackageType(String value) {
        return new JAXBElement<String>(_PackageType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.vizzavi.net/chargingandpayments/message/1.0", name = "vendorId")
    public JAXBElement<String> createVendorId(String value) {
        return new JAXBElement<String>(_VendorId_QNAME, String.class, null, value);
    }

}

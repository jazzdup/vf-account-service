//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.06 at 06:47:13 PM GMT 
//


package com.vodafone.charging.accountservice.dto.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.vizzavi.net/chargingandpayments/message/1.0}Response"/&gt;
 *         &lt;element ref="{http://www.vizzavi.net/chargingandpayments/message/1.0}messagegroup"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Body")
public class Body {

    @XmlElement(name = "Response", namespace="")
    protected Response response;
    @XmlElement(name="messagegroup", namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected Messagegroup messagegroup;

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link Response }
     *     
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link Response }
     *     
     */
    public void setResponse(Response value) {
        this.response = value;
    }

    /**
     * Gets the value of the messagegroup property.
     * 
     * @return
     *     possible object is
     *     {@link Messagegroup }
     *     
     */
    public Messagegroup getMessagegroup() {
        return messagegroup;
    }

    /**
     * Sets the value of the messagegroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link Messagegroup }
     *     
     */
    public void setMessagegroup(Messagegroup value) {
        this.messagegroup = value;
    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.02.06 at 06:47:13 PM GMT 
//


package com.vodafone.charging.accountservice.domain.xml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="authCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ban" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="errId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="errDescription" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="userGroups"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="item" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="billingCycleDay" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="spId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="isPrepay" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="childSpId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="spType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "status",
    "authCode",
    "ban",
    "errId",
    "errDescription",
    "userGroups",
    "billingCycleDay",
    "spId",
    "isPrepay",
    "childSpId",
    "spType"
})
@XmlRootElement(name = "Response", namespace="")
public class Response {

    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0", required = true)
    protected String status;
    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected String authCode;
    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected String ban;
    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected String errId;
    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected String errDescription;
    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected Response.UserGroups userGroups;
    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected int billingCycleDay;
    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected String spId;
    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected String isPrepay;
    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected String childSpId;
    @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
    protected String spType;

    /**
     * Gets the value of the status property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the authCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAuthCode() {
        return authCode;
    }

    /**
     * Sets the value of the authCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAuthCode(String value) {
        this.authCode = value;
    }

    /**
     * Gets the value of the ban property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBan() {
        return ban;
    }

    /**
     * Sets the value of the ban property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBan(String value) {
        this.ban = value;
    }

    /**
     * Gets the value of the errId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getErrId() {
        return errId;
    }

    /**
     * Sets the value of the errId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setErrId(String value) {
        this.errId = value;
    }

    /**
     * Gets the value of the errDescription property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getErrDescription() {
        return errDescription;
    }

    /**
     * Sets the value of the errDescription property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setErrDescription(String value) {
        this.errDescription = value;
    }

    /**
     * Gets the value of the userGroups property.
     *
     * @return
     *     possible object is
     *     {@link Response.UserGroups }
     *
     */
    public Response.UserGroups getUserGroups() {
        return userGroups;
    }

    /**
     * Sets the value of the userGroups property.
     *
     * @param value
     *     allowed object is
     *     {@link Response.UserGroups }
     *
     */
    public void setUserGroups(Response.UserGroups value) {
        this.userGroups = value;
    }

    /**
     * Gets the value of the billingCycleDay property.
     * 
     */
    public int getBillingCycleDay() {
        return billingCycleDay;
    }

    /**
     * Sets the value of the billingCycleDay property.
     * 
     */
    public void setBillingCycleDay(int value) {
        this.billingCycleDay = value;
    }

    /**
     * Gets the value of the spId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpId() {
        return spId;
    }

    /**
     * Sets the value of the spId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpId(String value) {
        this.spId = value;
    }

    /**
     * Gets the value of the isPrepay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsPrepay() {
        return isPrepay;
    }

    /**
     * Sets the value of the isPrepay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsPrepay(String value) {
        this.isPrepay = value;
    }

    /**
     * Gets the value of the childSpId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildSpId() {
        return childSpId;
    }

    /**
     * Sets the value of the childSpId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildSpId(String value) {
        this.childSpId = value;
    }

    /**
     * Gets the value of the spType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpType() {
        return spType;
    }

    /**
     * Sets the value of the spType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpType(String value) {
        this.spType = value;
    }


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
     *         &lt;element name="item" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "item"
    })
    public static class UserGroups {

        @XmlElement(namespace="http://www.vizzavi.net/chargingandpayments/message/1.0")
        protected List<String> item;

        /**
         * Gets the value of the item property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getItem() {
            if (item == null) {
                item = new ArrayList<String>();
            }
            return this.item;
        }

    }

}

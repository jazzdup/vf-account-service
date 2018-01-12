package com.vodafone.charging.accountservice.domain;

import lombok.ToString;

import java.util.List;

/**
 * Created by al on 12/01/18.
 */
@ToString
public class ERIFResponse {
    private String status;
    private String ban;
    private String errId;
    private String errDescription;
    private Integer billingCycleDay;
    private String serviceProviderId;
    private String childServiceProviderId;
    private String serviceProviderType;
    private boolean isPrepay;
    private List<String> usergroups;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBan() {
        return ban;
    }

    public void setBan(String ban) {
        this.ban = ban;
    }

    public String getErrId() {
        return errId;
    }

    public void setErrId(String errId) {
        this.errId = errId;
    }

    public String getErrDescription() {
        return errDescription;
    }

    public void setErrDescription(String errDescription) {
        this.errDescription = errDescription;
    }

    public Integer getBillingCycleDay() {
        return billingCycleDay;
    }

    public void setBillingCycleDay(Integer billingCycleDay) {
        this.billingCycleDay = billingCycleDay;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(String serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getChildServiceProviderId() {
        return childServiceProviderId;
    }

    public void setChildServiceProviderId(String childServiceProviderId) {
        this.childServiceProviderId = childServiceProviderId;
    }

    public String getServiceProviderType() {
        return serviceProviderType;
    }

    public void setServiceProviderType(String serviceProviderType) {
        this.serviceProviderType = serviceProviderType;
    }

    public boolean isPrepay() {
        return isPrepay;
    }

    public void setPrepay(boolean prepay) {
        isPrepay = prepay;
    }

    public List<String> getUsergroups() {
        return usergroups;
    }

    public void setUsergroups(List<String> usergroups) {
        this.usergroups = usergroups;
    }
}

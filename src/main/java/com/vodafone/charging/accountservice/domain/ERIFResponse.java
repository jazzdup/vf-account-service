package com.vodafone.charging.accountservice.domain;

import lombok.Builder;
import lombok.ToString;

import java.util.List;


@ToString
@Builder
public class ERIFResponse {
    private String status;
    private String ban;
    private String errId;
    private String errDescription;
    private Integer billingCycleDay;
    private String serviceProviderId;
    private String childServiceProviderId;
    private String serviceProviderType;
    private String customerType; //PRE or POST
    private List<String> usergroups;

    public String getStatus() {
        return status;
    }

    public String getBan() {
        return ban;
    }

    public String getErrId() {
        return errId;
    }

    public String getErrDescription() {
        return errDescription;
    }

    public Integer getBillingCycleDay() {
        return billingCycleDay;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public String getChildServiceProviderId() {
        return childServiceProviderId;
    }

    public String getServiceProviderType() {
        return serviceProviderType;
    }

    public String getCustomerType(){ return customerType; }

    public List<String> getUsergroups() {
        return usergroups;
    }

}

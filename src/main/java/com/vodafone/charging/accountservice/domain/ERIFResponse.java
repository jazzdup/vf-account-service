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

    public boolean isPrepay() {
        return isPrepay;
    }

    public List<String> getUsergroups() {
        return usergroups;
    }

}

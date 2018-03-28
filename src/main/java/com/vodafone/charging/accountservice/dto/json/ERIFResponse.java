package com.vodafone.charging.accountservice.dto.json;

import lombok.Builder;
import lombok.ToString;

import java.util.List;

/**
 * the names of the fields match the json field names, don't change them!
 */
@ToString
@Builder
public class ERIFResponse {
    private String status;
    private String ban;
    private String errId;
    private String errDescription;
    private Integer billingCycleDay;
    private String spId;
    private String childSpId;
    private String spType;
    private String isPrepay; //PRE or POST
    private List<String> userGroups;

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

    public String getSpId() {
        return spId;
    }

    public String getChildSpId() {
        return childSpId;
    }

    public String getSpType() {
        return spType;
    }

    public String getIsPrepay(){ return isPrepay; }

    public List<String> getUserGroups() {
        return userGroups;
    }

}

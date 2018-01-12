package com.vodafone.charging.accountservice.domain;

import lombok.ToString;

/**
 * Created by al on 12/01/18.
 */
@ToString
public class ERIFResponse {
    private String status;
    private String ban;
    private String errId;
    private Integer billingCycleDay;

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

    public Integer getBillingCycleDay() {
        return billingCycleDay;
    }

    public void setBillingCycleDay(Integer billingCycleDay) {
        this.billingCycleDay = billingCycleDay;
    }
}

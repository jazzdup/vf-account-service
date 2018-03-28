package com.vodafone.charging.accountservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter @ToString
public class PaymentApproval {
    private boolean success;
    private String description;
}

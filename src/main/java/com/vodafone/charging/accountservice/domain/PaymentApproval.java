package com.vodafone.charging.accountservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Builder
@Getter @ToString
public class PaymentApproval {
    private boolean success;
    private int responseCode;
    private String description;
}

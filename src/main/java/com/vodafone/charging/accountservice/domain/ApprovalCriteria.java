package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.accountservice.domain.enums.PaymentApprovalRule;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Incoming requests from clients can optionally provide
 */
@Builder
@Getter
//@Component
public class ApprovalCriteria {

    //    @Autowired
    private List<PaymentApprovalRule> paymentApprovalRules;
}

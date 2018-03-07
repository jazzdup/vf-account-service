package com.vodafone.charging.accountservice.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * Incoming requests from clients can optionally provide
 */
@Builder
@Getter
@Component
public class ApprovalCriteria {
//    @Autowired
//    private List<PaymentApprovalRules> paymentApprovalRules;
}

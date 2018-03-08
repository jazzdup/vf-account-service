package com.vodafone.charging.accountservice.domain.enums;

/**
 * Rules that can be triggered from client applications
 * for payment approvals
 */
//@Component
public enum PaymentApprovalRule {

    USE_ACCOUNT_BILLING_CYCLE_DAY,
    USE_RENEWAL_TRANSACTIONS;

    PaymentApprovalRule() {
    }
}

package com.vodafone.charging.accountservice.dto.er;

/**
 * Types of Transactions available in ER Core
 */
public enum ERTransactionType {

    PURCHASE,
    USAGE,
    REFUND,
    RENEWAL,
    GOODWILL_CREDIT,
    MODIFY;

    ERTransactionType() {
    }

}

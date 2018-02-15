package com.vodafone.charging.accountservice.domain.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Transaction {
    private long id;
    private long erTransactionId;
}

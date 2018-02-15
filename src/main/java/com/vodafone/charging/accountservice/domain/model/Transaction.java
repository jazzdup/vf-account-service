package com.vodafone.charging.accountservice.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter @ToString
public class Transaction {
    private long id;
    private long erTransactionId;
}

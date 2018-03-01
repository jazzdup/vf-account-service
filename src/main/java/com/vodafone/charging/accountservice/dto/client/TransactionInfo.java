package com.vodafone.charging.accountservice.dto.client;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Builder
@Getter
@ToString
public class TransactionInfo {

    @NonNull
    @Qualifier("transactionAmount")
    private BigDecimal amount;

}

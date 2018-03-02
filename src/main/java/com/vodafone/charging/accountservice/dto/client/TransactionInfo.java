package com.vodafone.charging.accountservice.dto.client;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Builder
@Getter
@ToString
public class TransactionInfo {

    @NonNull
    private BigDecimal amount;

}

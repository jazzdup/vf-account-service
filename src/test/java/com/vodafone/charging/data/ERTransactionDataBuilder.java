package com.vodafone.charging.data;

import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;

public class ERTransactionDataBuilder {

    public static ERTransaction anErTransaction() {
        return ERTransaction.builder().amount(BigDecimal.valueOf(new Random().nextDouble()).setScale(2, RoundingMode.HALF_UP))
                .dateTime(LocalDateTime.now().minusHours(1))
                .partnerId("test-partnerId")
                .type(ERTransactionType.PURCHASE.name())
                .build();
    }

    public static ERTransaction anErTransaction(BigDecimal amount, LocalDateTime localDateTime, ERTransactionType type) {
        return ERTransaction.builder().amount(amount)
                .dateTime(localDateTime)
                .partnerId("test-partnerId")
                .type(type.name())
                .build();
    }

}

package com.vodafone.charging.accountservice.dto.er;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a Transaction from ER Core Service
 */
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class ERTransaction {

    private BigDecimal amount;
    private LocalDateTime dateTime;
    private String partnerId;
    private String type;

}

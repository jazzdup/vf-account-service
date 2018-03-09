package com.vodafone.charging.accountservice.dto.er;

import com.vodafone.charging.accountservice.domain.ChargingId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Criteria used to search Transactions from ER Core
 */
@Builder
@Getter
@Setter
public class ERTransactionCriteria {

    private ChargingId chargingId;
    private Locale locale;
    private List<String> transactionTypes;
    private boolean monetaryOnly;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private int requiredResultSize;

}

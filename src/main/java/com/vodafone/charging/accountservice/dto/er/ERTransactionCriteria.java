package com.vodafone.charging.accountservice.dto.er;

import com.vodafone.charging.accountservice.domain.ChargingId;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.annotation.Nullable;
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

    @NonNull
    private ChargingId chargingId;
    @NonNull
    private Locale locale;
    @NonNull
    private List<String> transactionTypes;
    private boolean monetaryOnly;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    @Nullable
    private Integer requiredResultSize;

}

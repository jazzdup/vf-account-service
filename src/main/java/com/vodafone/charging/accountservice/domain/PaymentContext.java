package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.accountservice.dto.rest.CatalogInfo;
import com.vodafone.charging.accountservice.dto.rest.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * Context information about a payment (e.g. transaction details, any account attributes required, catalogInfo)
 */
@Component
@Builder @Getter
public class PaymentContext {

    private ChargingId chargingId;
    private TransactionInfo transactionInfo;
    private CatalogInfo catalogInfo;

}

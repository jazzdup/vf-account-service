package com.vodafone.charging.data.builder;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.domain.ApprovalCriteria;
import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.domain.SpendLimitInfo;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.dto.client.CatalogInfo;
import com.vodafone.charging.accountservice.dto.client.TransactionInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.SpendLimitDataBuilder.aSpendLimitInfo;

public class PaymentContextDataBuilder {

    public static PaymentContext aPaymentContext() {

        List<SpendLimitInfo> defaultSpendLimits =
                Lists.newArrayList(aSpendLimitInfo(2, SpendLimitType.ACCOUNT_TX),
                aSpendLimitInfo(20, SpendLimitType.ACCOUNT_DAY),
                aSpendLimitInfo(50, SpendLimitType.ACCOUNT_MONTH));

        return PaymentContext.builder().locale(Locale.UK)
                .chargingId(aChargingId())
                .transactionInfo(TransactionInfo.builder()
                        .amount(new BigDecimal(2.0))
                        .build())
                .catalogInfo(CatalogInfo.builder()
                        .defaultSpendLimitInfo(defaultSpendLimits)
                        .build())
                .build();

    }
    public static PaymentContext aPaymentContext(BigDecimal amount) {
        List<SpendLimitInfo> defaultSpendLimits =
                Lists.newArrayList(aSpendLimitInfo(2, SpendLimitType.ACCOUNT_TX),
                        aSpendLimitInfo(20, SpendLimitType.ACCOUNT_DAY),
                        aSpendLimitInfo(50, SpendLimitType.ACCOUNT_MONTH));

        return PaymentContext.builder().locale(Locale.UK)
                .chargingId(aChargingId())
                .transactionInfo(TransactionInfo.builder()
                        .amount(amount)
                        .build())
                .catalogInfo(CatalogInfo.builder()
                        .defaultSpendLimitInfo(defaultSpendLimits)
                        .build())
                .build();
    }

    public static PaymentContext aPaymentContext(List<SpendLimitInfo> defaultSpendLimits, BigDecimal paymentAmount) {
        return PaymentContext.builder().locale(Locale.UK)
                .chargingId(aChargingId())
                .transactionInfo(TransactionInfo.builder()
                        .amount(paymentAmount)
                        .build())
                .catalogInfo(CatalogInfo.builder()
                        .defaultSpendLimitInfo(defaultSpendLimits)
                        .build())
                .build();
    }

    public static PaymentContext aPaymentContext(List<SpendLimitInfo> defaultSpendLimits, BigDecimal paymentAmount, ApprovalCriteria approvalCriteria) {
        return PaymentContext.builder().locale(Locale.UK)
                .chargingId(aChargingId())
                .transactionInfo(TransactionInfo.builder()
                        .amount(paymentAmount)
                        .build())
                .catalogInfo(CatalogInfo.builder()
                        .defaultSpendLimitInfo(defaultSpendLimits)
                        .build())
                .approvalCriteria(approvalCriteria)
                .build();
    }

    public static PaymentContext aPaymentContextWithApprovalCriteria(ApprovalCriteria approvalCriteria) {

        List<SpendLimitInfo> defaultSpendLimits =
                Lists.newArrayList(aSpendLimitInfo(2, SpendLimitType.ACCOUNT_TX),
                        aSpendLimitInfo(20, SpendLimitType.ACCOUNT_DAY),
                        aSpendLimitInfo(50, SpendLimitType.ACCOUNT_MONTH));

        return PaymentContext.builder().locale(Locale.UK)
                .chargingId(aChargingId())
                .transactionInfo(TransactionInfo.builder()
                        .amount(new BigDecimal(2.0))
                        .build())
                .catalogInfo(CatalogInfo.builder()
                        .defaultSpendLimitInfo(defaultSpendLimits)
                        .build())
                .approvalCriteria(approvalCriteria)
                .build();

    }
}

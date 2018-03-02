package com.vodafone.charging.accountservice.service;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.client.TransactionInfo;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import static java.util.stream.Collectors.toList;

/**
 * Responsible for checking different types of Spend Limits e.g.
 * - transaction limits
 * - duration limits
 */
@Service
public class SpendLimitChecker {

    @Autowired
    private TimeZone timeZone;

    @Autowired
    private ERDateCalculator erDateCalculator;

    /**
     * Check the current transaction does not breach the transaction limit.
     * If no transaction limit is set then check if a default transaction limit has been set.
     */
    public SpendLimitResult checkTransactionLimit(@NonNull List<SpendLimit> spendLimits,
                                                  @NonNull List<SpendLimit> defaultSpendLimits,
                                                  @NonNull List<TransactionInfo> transactions,
                                                  @NonNull SpendLimitType spendLimitType) {

        final BigDecimal totalTxAmount = transactions.stream()
                .filter(Objects::nonNull)
                .map(TransactionInfo::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        List<SpendLimit> defaultLimits = Lists.newArrayList();

        final List<SpendLimit> limits = spendLimits.stream()
                .filter(l -> l.getSpendLimitType().equals(spendLimitType))
                .collect(toList());

        //Check standard SpendLimit
        if (!limits.isEmpty() && limits.get(0).getLimit() < totalTxAmount.doubleValue()) {
            return SpendLimitResult.builder().success(false)
                    .failureCauseType(spendLimitType)
                    .failureReason(spendLimitType.name() + " spend limit breached")
                    .appliedLimitValue(limits.get(0).getLimit())
                    .totalTransactionsValue(totalTxAmount.setScale(2, RoundingMode.HALF_UP).doubleValue())
                    .build();
        } else if (limits.isEmpty()) {
            //Check default limit
            defaultLimits = defaultSpendLimits.stream()
                    .filter(l -> l.getSpendLimitType().equals(spendLimitType))
                    .collect(toList());

            if (!defaultLimits.isEmpty() && defaultLimits.get(0).getLimit() < totalTxAmount.doubleValue())
                return SpendLimitResult.builder().success(false)
                        .failureCauseType(spendLimitType)
                        .failureReason(spendLimitType.name() + " default spend limit breached")
                        .appliedLimitValue(defaultLimits.get(0).getLimit())
                        .totalTransactionsValue(totalTxAmount.setScale(2, RoundingMode.HALF_UP).doubleValue())
                        .build();
        }

        BigDecimal totalTxValue = Objects.nonNull(totalTxAmount) ? totalTxAmount : BigDecimal.ZERO;
        double appliedLimitValue;

        if (!limits.isEmpty()) {
            appliedLimitValue = limits.get(0).getLimit();
        } else if (limits.isEmpty() && !defaultLimits.isEmpty()) {
            appliedLimitValue = defaultLimits.get(0).getLimit();
        } else {
            appliedLimitValue = 0.0;
        }

        return SpendLimitResult.successResponse(appliedLimitValue, totalTxValue.setScale(2, RoundingMode.HALF_UP).doubleValue());
    }

    /**
     * Check the currentTransction plus all transactions in a given duration do not breach the limit set for that duration.
     * If no duration limit has been set, check if a default limit has been set for that duration.
     */
    public SpendLimitResult checkDurationLimit(@NonNull List<SpendLimit> spendLimits,
                                               @NonNull List<SpendLimit> defaultSpendLimits,
                                               @NonNull List<ERTransaction> tx,
                                               @NonNull BigDecimal currentTransactionAmount,
                                               @NonNull final SpendLimitType spendLimitType,
                                               int billingCycleDay) {

        LocalDateTime start;
        LocalDateTime end;
        if (spendLimitType.equals(SpendLimitType.ACCOUNT_DAY)) {
            start = LocalDateTime.of(LocalDate.now(timeZone.toZoneId()), LocalTime.MIDNIGHT);
            end = LocalDateTime.of(LocalDate.now(timeZone.toZoneId()), LocalTime.MAX);
        } else {
            Map<String, LocalDateTime> dates = erDateCalculator.calculateBillingCycleDates(billingCycleDay);
            start = dates.get("startDate");
            end = dates.get("endDate");
        }

        //Collect relevant payments
        List<ERTransaction> payments =
                tx.stream().filter(transaction -> transaction.getDateTime().isAfter(start)
                        && transaction.getDateTime().isBefore(end)
                        && !transaction.getType().equalsIgnoreCase(ERTransactionType.REFUND.name()))
                        .collect(toList());

        List<ERTransaction> refundTransactions =
                tx.stream().filter(transaction -> transaction.getDateTime().isAfter(start)
                        && transaction.getDateTime().isBefore(end)
                        && transaction.getType().equalsIgnoreCase(ERTransactionType.REFUND.name()))
                        .collect(toList());

        //total payments
        final BigDecimal totalPaymentAmount = payments.stream().map(ERTransaction::getAmount)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        //total refunds
        final BigDecimal totalRefundAmount = refundTransactions.stream().map(ERTransaction::getAmount)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        //calculate
        BigDecimal totalValue = totalPaymentAmount.subtract(totalRefundAmount);
        totalValue = totalValue.setScale(2, RoundingMode.HALF_UP);

        //Add the current transaction value including all previous transactions
        BigDecimal transactionsIncludingCurrent = totalValue.add(currentTransactionAmount);

        //find relevant limit to apply
        List<SpendLimit> limits = spendLimits.stream()
                .filter(limit -> limit.getSpendLimitType().equals(spendLimitType))
                .collect(toList());

        //check limit if exists otherwise check default
        if (!limits.isEmpty()
                && limits.get(0).getLimit() < transactionsIncludingCurrent.doubleValue()) {
            return SpendLimitResult.builder().success(false).failureCauseType(spendLimitType)
                    .failureReason(spendLimitType.name() + " spend limit breached")
                    .appliedLimitValue(limits.get(0).getLimit())
                    .totalTransactionsValue(transactionsIncludingCurrent.doubleValue())
                    .build();
        } else if (limits.isEmpty()) {//apply a default
            //get default
            limits = defaultSpendLimits.stream()
                    .filter(l -> l.getSpendLimitType().equals(spendLimitType))
                    .collect(toList());

            if (!limits.isEmpty() && limits.get(0).getLimit() < transactionsIncludingCurrent.doubleValue()) {
                return SpendLimitResult.builder().success(false)
                        .failureCauseType(spendLimitType)
                        .appliedLimitValue(limits.get(0).getLimit())
                        .totalTransactionsValue(transactionsIncludingCurrent.doubleValue())
                        .failureReason(spendLimitType.name() + " default spend limit breached").build();
            }
        }

        return SpendLimitResult.builder().success(true)
                .failureReason("")
                .appliedLimitValue(limits.isEmpty() ? 0.0 : limits.get(0).getLimit())
                .totalTransactionsValue(transactionsIncludingCurrent.doubleValue())
                .build();
    }

    public Map<String, List<ERTransaction>> groupTransactions(List<SpendLimit> spendLimits, List<SpendLimit> defaultSpendLimits, List<ERTransaction> tx) {

        final LocalDateTime start = LocalDateTime.of(LocalDate.now(timeZone.toZoneId()), LocalTime.MIDNIGHT);
        final LocalDateTime end = LocalDateTime.of(LocalDate.now(timeZone.toZoneId()), LocalTime.MAX);

//        Map<String, List<ERTransaction>> grouping =
//                tx.stream().filter(transaction -> transaction.getDateTime().isAfter(start)
//                        && transaction.getDateTime().isBefore(end))
//                        .collect(Collectors.groupingBy(ERTransaction::getType));

//        Map<String, Optional<BigDecimal>> grouping =
//                tx.stream().filter(transaction -> transaction.getDateTime().isAfter(start)
//                        && transaction.getDateTime().isBefore(end))
//                        .collect(groupingBy(ERTransaction::getType, maxBy(ERTransaction::getAmount)));
//
//        return grouping;
        throw new UnsupportedOperationException();

    }


}

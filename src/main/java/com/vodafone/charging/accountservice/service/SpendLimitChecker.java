package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.ApprovalCriteria;
import com.vodafone.charging.accountservice.domain.PaymentContext;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.client.CatalogInfo;
import com.vodafone.charging.accountservice.dto.client.TransactionInfo;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.accountservice.domain.enums.PaymentApprovalRule.USE_RENEWAL_TRANSACTIONS;
import static java.util.Optional.ofNullable;
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

        List<SpendLimit> defaultLimits = newArrayList();

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
        double appliedLimitValue = findAppliedLimit(limits, defaultLimits);

        return SpendLimitResult.successResponse(appliedLimitValue, totalTxValue.setScale(2, RoundingMode.HALF_UP).doubleValue());
    }

    /**
     * Check the currentTransction plus all transactions in a given duration do not breach the limit set for that duration.
     * If no duration limit has been set, check if a default limit has been set for that duration.
     */
    public SpendLimitResult checkDurationLimit(@NonNull PaymentContext paymentContext,
                                               @NonNull List<SpendLimit> spendLimits,
                                               @NonNull List<ERTransaction> erTransList,
                                               @NonNull final SpendLimitType spendLimitType,
                                               int billingCycleDay) {


        Optional<CatalogInfo> infoOptional = ofNullable(paymentContext.getCatalogInfo());

        final List<SpendLimit> defaultSpendLimits = SpendLimit.fromSpendLimitsInfo(infoOptional
                .orElse(CatalogInfo.builder().defaultSpendLimitInfo(newArrayList())
                        .build())
                .getDefaultSpendLimitInfo());
        final BigDecimal currentTransactionAmount = paymentContext.getTransactionInfo().getAmount();
        final Map<String, LocalDateTime> startEndDates = erDateCalculator.calculateDurationSpendLimitDates(spendLimitType, billingCycleDay);

        final LocalDateTime start = startEndDates.get(ERDateCalculator.START_DATE_KEY);
        final LocalDateTime end = startEndDates.get(ERDateCalculator.END_DATE_KEY);

        //Collect relevant payments
        List<ERTransaction> payments =
                erTransList.stream().filter(transaction -> transaction.getDateTime().isAfter(start)
                        && transaction.getDateTime().isBefore(end)
                        && !transaction.getType().equalsIgnoreCase(ERTransactionType.REFUND.name()))
                        .collect(toList());

        List<ERTransaction> refundTransactions =
                erTransList.stream().filter(transaction -> transaction.getDateTime().isAfter(start)
                        && transaction.getDateTime().isBefore(end)
                        && transaction.getType().equalsIgnoreCase(ERTransactionType.REFUND.name()))
                        .collect(toList());

        //total payments
        final BigDecimal totalPaymentAmount = payments.stream().map(ERTransaction::getAmount)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

//        final BigDecimal totalPaymentAmount = mapReducePayments(erTransList, buildPaymentsPredicate(paymentContext, start, end));

        //total refunds
        final BigDecimal totalRefundAmount = refundTransactions.stream().map(ERTransaction::getAmount)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        //calculate
//        BigDecimal totalValue = totalPaymentAmount.subtract(totalRefundAmount).add(currentTransactionAmount);
        final BigDecimal transactionsIncludingCurrent = totalPaymentAmount.subtract(totalRefundAmount).add(currentTransactionAmount);

        //Add the current transaction value including all previous transactions
//        BigDecimal transactionsIncludingCurrent = totalValue.add(currentTransactionAmount);

        //find relevant limit to apply
        final List<SpendLimit> limits = spendLimits.stream()
                .filter(limit -> limit.getSpendLimitType().equals(spendLimitType))
                .collect(toList());

        List<SpendLimit> filteredDefaultLimits = newArrayList();

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
            filteredDefaultLimits = defaultSpendLimits.stream()
                    .filter(l -> l.getSpendLimitType().equals(spendLimitType))
                    .collect(toList());

            if (!filteredDefaultLimits.isEmpty() && filteredDefaultLimits.get(0).getLimit() < transactionsIncludingCurrent.doubleValue()) {
                return SpendLimitResult.builder().success(false)
                        .failureCauseType(spendLimitType)
                        .appliedLimitValue(filteredDefaultLimits.get(0).getLimit())
                        .totalTransactionsValue(transactionsIncludingCurrent.doubleValue())
                        .failureReason(spendLimitType.name() + " default spend limit breached").build();
            }
        }

        double appliedLimitValue = findAppliedLimit(limits, filteredDefaultLimits);

        return SpendLimitResult.builder().success(true)
                .failureReason("")
                .appliedLimitValue(appliedLimitValue)
                .totalTransactionsValue(transactionsIncludingCurrent.doubleValue())
                .build();
    }

    public Predicate<ERTransaction> buildPaymentsPredicate(PaymentContext context, LocalDateTime start, LocalDateTime end) {

        final Predicate<ERTransaction> dates = date -> date.getDateTime().isAfter(start) && date.getDateTime().isBefore(end);

        Predicate<ERTransaction> allPayments;
        Predicate<ERTransaction> minusRenewals;

        Optional<ApprovalCriteria> rulesOptional = ofNullable(context.getApprovalCriteria());

        allPayments = payment ->
                payment.getType().equalsIgnoreCase(ERTransactionType.RENEWAL.name()) ||
                        payment.getType().equalsIgnoreCase(ERTransactionType.USAGE.name()) ||
                        payment.getType().equalsIgnoreCase(ERTransactionType.PURCHASE.name());

        if (!rulesOptional.isPresent()
                || !context.getApprovalCriteria().getPaymentApprovalRules().contains(USE_RENEWAL_TRANSACTIONS)) {
            minusRenewals = payment ->
                    !payment.getType().equalsIgnoreCase(ERTransactionType.RENEWAL.name());

            return dates.and(allPayments).and(minusRenewals);
        }

        return dates.and(allPayments);
    }

    public BigDecimal mapReducePayments(final List<ERTransaction> erTransList,
                                        final Predicate<ERTransaction> predicate) {

        return erTransList.stream().filter(predicate)
                .map(ERTransaction::getAmount)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    private double findAppliedLimit(List<SpendLimit> limits, List<SpendLimit> defaultLimits) {
        double appliedLimitValue;
        if (!limits.isEmpty()) {
            appliedLimitValue = limits.get(0).getLimit();
        } else if (!defaultLimits.isEmpty()) {
            appliedLimitValue = defaultLimits.get(0).getLimit();
        } else {
            appliedLimitValue = 0.0;
        }
        return appliedLimitValue;
    }
}

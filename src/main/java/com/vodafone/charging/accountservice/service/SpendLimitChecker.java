package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.dto.client.TransactionInfo;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;
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

    /**
     * Check the current transaction does not breach the transaction limit.
     * If no transaction limit is set then check if a default transaction limit has been set.
     */
    public boolean checkTransactionLimit(List<SpendLimit> spendLimits, List<SpendLimit> defaultSpendLimits, List<TransactionInfo> transactions) {

        final BigDecimal totalTxAmount = transactions.stream()
                .filter(Objects::nonNull)
                .map(TransactionInfo::getAmount)
                .reduce(BigDecimal::add).orElse(null);

        final List<SpendLimit> limits = spendLimits.stream()
                .filter(l -> l.getSpendLimitType().equals(SpendLimitType.ACCOUNT_TX))
                .collect(toList());

        //Check standard SpendLimit
        if (null != totalTxAmount && !limits.isEmpty() && limits.size() == 1
                && limits.get(0).getLimit() < Double.valueOf(totalTxAmount.toString())) {
            return false; //breached!
        } else if (null != totalTxAmount) {
            //Check Default
            final List<SpendLimit> defaultLimits = defaultSpendLimits.stream()
                    .filter(l -> l.getSpendLimitType().equals(SpendLimitType.ACCOUNT_TX))
                    .collect(toList());

            return defaultLimits.isEmpty() || defaultLimits.get(0).getLimit() != 1
                    || defaultLimits.get(0).getLimit() >= Double.valueOf(totalTxAmount.toString());
        }
        return true;
    }

    /**
     * Check the currentTransction plus all transactions in a given duration do not breach the limit set for that duration.
     * If no duration limit has been set, check if a default limit has been set for that duration.
     */
    public boolean checkDurationLimit(List<SpendLimit> spendLimits, List<SpendLimit> defaultSpendLimits, List<ERTransaction> tx, BigDecimal currentTransactionAmount) {

        final LocalDateTime start = LocalDateTime.of(LocalDate.now(timeZone.toZoneId()), LocalTime.MIDNIGHT);
        final LocalDateTime end = LocalDateTime.of(LocalDate.now(timeZone.toZoneId()), LocalTime.MAX);

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
        final List<SpendLimit> limits = spendLimits.stream()
                .filter(limit -> limit.getSpendLimitType().equals(SpendLimitType.ACCOUNT_DAY))
                .collect(toList());

        //check limit if exists otherwise check default
        if (!limits.isEmpty()
                && limits.get(0).getLimit() < transactionsIncludingCurrent.doubleValue()) {
            return false;
        } else if (limits.isEmpty()) {//apply a default

            //get default
            final List<SpendLimit> defaultLimits = defaultSpendLimits.stream()
                    .filter(l -> l.getSpendLimitType().equals(SpendLimitType.ACCOUNT_DAY))
                    .collect(toList());

            if (!defaultLimits.isEmpty() && defaultLimits.get(0).getLimit() < transactionsIncludingCurrent.doubleValue()) {
                return false;
            }
        }
        return true;
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

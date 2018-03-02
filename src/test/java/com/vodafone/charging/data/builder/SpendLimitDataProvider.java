package com.vodafone.charging.data.builder;

import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.data.ERTransactionDataBuilder.anErTransaction;

/**
 * Provides test data for the spend limits tests
 */
public class SpendLimitDataProvider {

    /**
     * Standard set of transactions with a mix of all types and some that are not within the current day.
     * purchases total 16, refunds 6.3, net payments = 9.7
     */
    public static List<ERTransaction> anERTransactionListForCurrentDay() {

        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(5.0), LocalDateTime.now(), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.5), LocalDateTime.now(), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.5), LocalDateTime.now(), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(10), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(6.3), LocalDateTime.now(), ERTransactionType.REFUND),//include and subtract
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(2), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(3), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(4), ERTransactionType.REFUND));//exclude

        return transactions;

    }

    public static List<ERTransaction> anERTransactionListForCurrentMonth() {

        throw new UnsupportedOperationException();

    }

}

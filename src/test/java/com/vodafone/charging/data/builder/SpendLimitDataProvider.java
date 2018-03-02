package com.vodafone.charging.data.builder;

import com.google.common.collect.Maps;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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

    public static List<ERTransaction> anERTransactionListWithinDates(LocalDateTime start, LocalDateTime end) {

        //payments=65.5, refunds 15.8, total 49.7 - month spend limit = 50
        final List<ERTransaction> transactions = newArrayList(
                //value = 55.5
                anErTransaction(new BigDecimal(11.1), start.plusMinutes(10), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(22.1), start.plusHours(10), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), start.plusDays(10), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), start.plusDays(21), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.1), start.plusHours(20), ERTransactionType.USAGE),//include

                //value = 10
                anErTransaction(new BigDecimal(0.1), start.plusHours(20), ERTransactionType.RENEWAL),//include
                anErTransaction(new BigDecimal(0.3), start.plusDays(2), ERTransactionType.RENEWAL),//include
                anErTransaction(new BigDecimal(4.2), end.minusSeconds(4), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(3.3), end.minusMinutes(52), ERTransactionType.RENEWAL),//include
                anErTransaction(new BigDecimal(2.1), start.plusHours(50), ERTransactionType.USAGE),//include

                //all excluded
                anErTransaction(new BigDecimal(0.1), end.plusHours(20), ERTransactionType.RENEWAL),//exclude
                anErTransaction(new BigDecimal(0.3), end.plusDays(2), ERTransactionType.RENEWAL),//exclude
                anErTransaction(new BigDecimal(4.2), start.minusSeconds(4), ERTransactionType.PURCHASE),//exclude
                anErTransaction(new BigDecimal(3.3), start.minusMinutes(52), ERTransactionType.RENEWAL),//exclude
                anErTransaction(new BigDecimal(2.1), end.plusHours(50), ERTransactionType.USAGE),//exclude

                //value = -15.8
                anErTransaction(new BigDecimal(10.1), end.plusSeconds(1), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(10.1), end.plusHours(1), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(5.3), end.minusSeconds(5), ERTransactionType.REFUND),//include
                anErTransaction(new BigDecimal(10.5), end.minusDays(20), ERTransactionType.REFUND),//include
                anErTransaction(new BigDecimal(20.1), start.minusSeconds(1), ERTransactionType.REFUND));//exclude

        return transactions;
    }

    /*
    This is for test purposes only. We assume that the billingCycleDate is always for this month
     */
    public static Map<String, LocalDateTime> getBillingCycleDates(int billingCycleDay) {

        LocalDate now = LocalDate.now();
        LocalDateTime fifthOfthisMonth = LocalDateTime.of(now, LocalTime.MIDNIGHT).withDayOfMonth(billingCycleDay);
        LocalDateTime fifthOfNextMonth = LocalDateTime.of(now, LocalTime.MAX).plusMonths(1).withDayOfMonth(billingCycleDay);
        Map<String, LocalDateTime> billingCycleDates = Maps.newHashMap();
        String startKey = "startDate";
        String endKey = "endDate";
        billingCycleDates.put(startKey, fifthOfthisMonth);
        billingCycleDates.put(endKey, fifthOfNextMonth);

        return billingCycleDates;

    }



}

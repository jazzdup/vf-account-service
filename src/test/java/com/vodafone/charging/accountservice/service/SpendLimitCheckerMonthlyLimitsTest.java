package com.vodafone.charging.accountservice.service;

import com.google.common.collect.Maps;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.data.ERTransactionDataBuilder.anErTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;

/**
 * Tests the monthly account spend limit functionality
 */
public class SpendLimitCheckerMonthlyLimitsTest extends SpendLimitCheckerBase {

    @Test
    public void shouldNotBreachWhenMonthLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimit() {
        //given
        //payments=65.5, currentTx 0.3, refunds 15.8, total 50.0 - spend limit = 50
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(21.1), LocalDateTime.now().minusHours(2), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(22.1), LocalDateTime.now().withDayOfMonth(1), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(4), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), LocalDateTime.now().withDayOfMonth(1), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().withDayOfMonth(1), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(40), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(50), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(5.3), LocalDateTime.now(), ERTransactionType.REFUND),//include
                anErTransaction(new BigDecimal(10.5), LocalDateTime.now(), ERTransactionType.REFUND),//include
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(4), ERTransactionType.REFUND));//exclude

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);
        given(erDateCalculator.calculateBillingCycleDates(anyInt())).willReturn(monthDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 1);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
    }

    @Test
    public void shouldNotBreachWhenMonthLimitDefinedAndBillingCycleDayDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimit() {

        //given
        LocalDate now = LocalDate.now();
        LocalDateTime fifthOfthisMonth = LocalDateTime.of(now, LocalTime.MIDNIGHT).withDayOfMonth(5);
        LocalDateTime fifthOfNextMonth = LocalDateTime.of(now, LocalTime.MAX).plusMonths(1).withDayOfMonth(5);
        Map<String, LocalDateTime> billingCycleDates = Maps.newHashMap();
        String startKey = "startDate";
        String endKey = "endDate";
        billingCycleDates.put(startKey, fifthOfthisMonth);
        billingCycleDates.put(endKey, fifthOfNextMonth);

        //payments=60.0 current Tx 0.3 refunds 10.0  (should calculate to 50.3) - spend limit = 50
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(19.7), billingCycleDates.get(startKey).plusMinutes(5), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(20.0), billingCycleDates.get(startKey).plusDays(20), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(endKey).minusSeconds(1), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(startKey).plusMinutes(5), ERTransactionType.RENEWAL),//include
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(startKey).minusSeconds(1), ERTransactionType.RENEWAL),//exclude
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(endKey).plusMinutes(5), ERTransactionType.RENEWAL),//exclude
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(startKey).plusHours(5), ERTransactionType.REFUND));//include

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(erDateCalculator.calculateBillingCycleDates(anyInt())).willReturn(billingCycleDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 5);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getAppliedLimitValue()).isEqualTo(50.0);
        assertThat(result.getTotalTransactionsValue()).isEqualTo(50.0);

    }

    @Test
    public void shouldBreachWhenMonthLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimitThenCurrentTxOverLimit() {
        //given
        //payments=65.5 current Tx 0.3 refunds 15.7  (should calculate to 50.1) - spend limit = 50
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(21.1), LocalDateTime.now(), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(22.1), LocalDateTime.now(), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now(), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), LocalDateTime.now(), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now(), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(40), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(50), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(5.2), LocalDateTime.now(), ERTransactionType.REFUND),//include
                anErTransaction(new BigDecimal(10.5), LocalDateTime.now(), ERTransactionType.REFUND),//include
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(4), ERTransactionType.REFUND));//exclude

        //expected calculations given data
        BigDecimal currentTransactionAmount = new BigDecimal(0.3);
        given(erDateCalculator.calculateBillingCycleDates(anyInt())).willReturn(monthDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 1);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_MONTH);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_MONTH.name());
        assertThat(result.getFailureReason()).doesNotContain("default spend limit");
    }


    /*
    To simplify testing we are assuming billingCycleDay is 5 and that it is this month
    The calculation logic is tested elsewhere
     */
    @Test
    public void shouldBreachWhenMonthLimitDefinedAndBillingCycleDayDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimitCurrentTxTakesItOver() {
        //given
        LocalDate now = LocalDate.now();
        LocalDateTime fifthOfthisMonth = LocalDateTime.of(now, LocalTime.MIDNIGHT).withDayOfMonth(5);
        LocalDateTime fifthOfNextMonth = LocalDateTime.of(now, LocalTime.MAX).plusMonths(1).withDayOfMonth(5);
        Map<String, LocalDateTime> billingCycleDates = Maps.newHashMap();
        String startKey = "startDate";
        String endKey = "endDate";
        billingCycleDates.put(startKey, fifthOfthisMonth);
        billingCycleDates.put(endKey, fifthOfNextMonth);

        //payments=60.0 current Tx 0.3 refunds 10.0  (should calculate to 50.3) - spend limit = 50
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(20.0), billingCycleDates.get(startKey).plusMinutes(5), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(20.0), billingCycleDates.get(startKey).plusDays(20), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(endKey).minusSeconds(1), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(startKey).plusMinutes(5), ERTransactionType.RENEWAL),//include
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(startKey).minusSeconds(1), ERTransactionType.RENEWAL),//exclude
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(endKey).plusMinutes(5), ERTransactionType.RENEWAL),//exclude
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(startKey).plusHours(5), ERTransactionType.REFUND));//include

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(erDateCalculator.calculateBillingCycleDates(anyInt())).willReturn(billingCycleDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 5);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_MONTH);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_MONTH.name());
        assertThat(result.getAppliedLimitValue()).isEqualTo(50.0);
        assertThat(result.getTotalTransactionsValue()).isEqualTo(50.3);

    }

    //TODO Add some default scenarios around monthly account limits
    @Test
    public void shouldNotBreachDefaultWhenNoMonthLimitDefinedButDefaultIsAndPaymentsGoOver() {
    }

    @Test
    public void shouldNotBreachMonthlyIfNoLimitsSet() {
    }

    @Test
    public void shouldBreachDefaultWhenNoMonthLimitDefinedButDefaultIsAndPaymentsGoOver() {
        //given
        LocalDate now = LocalDate.now();
        LocalDateTime fifthOfthisMonth = LocalDateTime.of(now, LocalTime.MIDNIGHT).withDayOfMonth(5);
        LocalDateTime fifthOfNextMonth = LocalDateTime.of(now, LocalTime.MAX).plusMonths(1).withDayOfMonth(5);
        Map<String, LocalDateTime> billingCycleDates = Maps.newHashMap();
        String startKey = "startDate";
        String endKey = "endDate";
        billingCycleDates.put(startKey, fifthOfthisMonth);
        billingCycleDates.put(endKey, fifthOfNextMonth);

        //payments=60.0 current Tx 0.3 refunds 10.0  (should calculate to 50.3) - spend limit = 50
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(25.0), billingCycleDates.get(startKey).plusMinutes(5), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(20.0), billingCycleDates.get(startKey).plusDays(20), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(endKey).minusSeconds(1), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(startKey).plusMinutes(5), ERTransactionType.RENEWAL),//include
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(startKey).minusSeconds(1), ERTransactionType.RENEWAL),//exclude
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(endKey).plusMinutes(5), ERTransactionType.RENEWAL),//exclude
                anErTransaction(new BigDecimal(10.0), billingCycleDates.get(startKey).plusHours(5), ERTransactionType.REFUND));//include

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(erDateCalculator.calculateBillingCycleDates(anyInt())).willReturn(billingCycleDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(newArrayList(), defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 5);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_MONTH);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_MONTH.name());
        assertThat(result.getAppliedLimitValue()).isEqualTo(55.0);
        assertThat(result.getTotalTransactionsValue()).isEqualTo(55.3);


    }

    @Ignore
    public void shouldGroupTransactions() {
        //DataSet
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(2), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(3), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(4), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(2), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusMinutes(10), ERTransactionType.REFUND));//include and subtract

        final Map<String, List<ERTransaction>> groupingMap =
                spendLimitChecker.groupTransactions(spendLimits, defaultSpendLimits, transactions);

    }


}

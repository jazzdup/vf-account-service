package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.data.ERTransactionDataBuilder.anErTransaction;
import static com.vodafone.charging.data.builder.SpendLimitDataProvider.anERTransactionListWithinDates;
import static com.vodafone.charging.data.builder.SpendLimitDataProvider.getBillingCycleDates;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

/**
 * Tests the monthly account spend limit functionality
 */
public class SpendLimitCheckerMonthlyLimitsTest extends SpendLimitCheckerBase {

    @Test
    public void shouldNotBreachWhenMonthLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimit() {
        //given
        //payments=65.5, refunds 15.8, currentTx 0.3 total 50 - month spend limit = 50
        final List<ERTransaction> transactions =
                anERTransactionListWithinDates(monthDates.get("startDate"), monthDates.get("endDate"));

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);
        double totalTxValue = 50.00;

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(monthDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 1);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getTotalTransactionsValue()).isEqualTo(totalTxValue);
        assertThat(result.getTotalTransactionsValue()).isEqualTo(spendLimits.get(2).getLimit().doubleValue());
    }

    @Test
    public void shouldNotBreachWhenMonthLimitDefinedAndBillingCycleDayDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimit() {

        //given
        Map<String, LocalDateTime> billingCycleDates = getBillingCycleDates(10);
        String startKey = "startDate";
        String endKey = "endDate";

        //payments=65.5, refunds 15.8, total 49.7 currentTx = 0.3- month spend limit = 50
        final List<ERTransaction> transactions = anERTransactionListWithinDates(billingCycleDates.get(startKey), billingCycleDates.get(endKey));

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(billingCycleDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 10);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getAppliedLimitValue()).isEqualTo(spendLimits.get(2).getLimit().doubleValue());
        assertThat(result.getTotalTransactionsValue()).isEqualTo(50.0);

    }

    @Test
    public void shouldBreachWhenMonthLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimitThenCurrentTxOverLimit() {
        //given
        //payments=65.5, refunds 15.8, currentTx 0.4 total 50.1 - month spend limit = 50
        final List<ERTransaction> transactions =
                anERTransactionListWithinDates(monthDates.get("startDate"), monthDates.get("endDate"));

        BigDecimal currentTransactionAmount = new BigDecimal(0.4);
        double totalTxValue = 50.1;

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(monthDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 1);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_MONTH);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_MONTH.name());
        assertThat(result.getFailureReason()).doesNotContain("default");
        assertThat(result.getTotalTransactionsValue()).isEqualTo(totalTxValue);
        assertThat(result.getAppliedLimitValue()).isEqualTo(spendLimits.get(2).getLimit().doubleValue());
    }


    /*
    To simplify testing we are assuming billingCycleDay is 5 and that it is this month
    The calculation logic is tested elsewhere
     */
    @Test
    public void shouldBreachWhenMonthLimitDefinedAndBillingCycleDayDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimitCurrentTxTakesItOver() {
        //given
        Map<String, LocalDateTime> billingCycleDates = getBillingCycleDates(10);
        String startKey = "startDate";
        String endKey = "endDate";

        //payments=65.5, refunds 15.8, currentTx = 0.4 total 50.1 - month spend limit = 50
        final List<ERTransaction> transactions = anERTransactionListWithinDates(billingCycleDates.get(startKey), billingCycleDates.get(endKey));

        BigDecimal currentTransactionAmount = new BigDecimal(0.4);

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(billingCycleDates);


        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 10);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_MONTH);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_MONTH.name());
        assertThat(result.getAppliedLimitValue()).isEqualTo(spendLimits.get(2).getLimit().doubleValue());
        assertThat(result.getTotalTransactionsValue()).isEqualTo(50.1);

    }

    @Test
    public void shouldNotBreachDefaultWhenNoMonthLimitDefinedButDefaultIsAndPaymentsGoOver() {

        //given
        //payments=65.5, refunds 15.8, currentTx 0.4 total 55.1 - month spend limit = 55
        final List<ERTransaction> transactions =
                anERTransactionListWithinDates(monthDates.get("startDate"), monthDates.get("endDate"));

        BigDecimal currentTransactionAmount = new BigDecimal(5.4);
        double totalTxValue = 55.1;

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(monthDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(newArrayList(), defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 1);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_MONTH);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_MONTH.name());
        assertThat(result.getAppliedLimitValue()).isEqualTo(defaultSpendLimits.get(2).getLimit().doubleValue());
        assertThat(result.getTotalTransactionsValue()).isEqualTo(totalTxValue);


    }

    @Test
    public void shouldNotBreachMonthlyIfNoLimitsSet() {

        //given
        //payments=65.5, refunds 15.8, currentTx 5000 total  - month spend limit = 55
        final List<ERTransaction> transactions =
                anERTransactionListWithinDates(monthDates.get("startDate"), monthDates.get("endDate"));

        BigDecimal currentTransactionAmount = new BigDecimal(5000);
        double totalTxValue = 5049.7;

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(monthDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(newArrayList(), newArrayList(), transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 1);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getAppliedLimitValue()).isEqualTo(0.0);
        assertThat(result.getTotalTransactionsValue()).isEqualTo(totalTxValue);
    }

    @Test
    public void shouldBreachDefaultWhenNoMonthLimitDefinedBillingCycleDayDefinedAndPaymentsGoOver() {
        //given
        Map<String, LocalDateTime> billingCycleDates = getBillingCycleDates(10);
        String startKey = "startDate";
        String endKey = "endDate";

        //payments=65.5, refunds 15.8, currentTx 0.4 total 55.1  - month spend limit = 55
        final List<ERTransaction> transactions = anERTransactionListWithinDates(billingCycleDates.get(startKey), billingCycleDates.get(endKey));

        BigDecimal currentTransactionAmount = new BigDecimal(5.4);
        double expectedTxValue = 55.1;

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(billingCycleDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(newArrayList(), defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 10);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_MONTH);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_MONTH.name());
        assertThat(result.getAppliedLimitValue()).isEqualTo(defaultSpendLimits.get(2).getLimit());
        assertThat(result.getTotalTransactionsValue()).isEqualTo(expectedTxValue);
    }

    @Test
    public void shouldApplyLimitsCheckWhenNoTransactionHistory() {
        //given
        Map<String, LocalDateTime> billingCycleDates = getBillingCycleDates(10);
        BigDecimal currentTransactionAmount = new BigDecimal(5.4);

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(billingCycleDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, newArrayList(), currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 10);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getAppliedLimitValue()).isEqualTo(spendLimits.get(2).getLimit().doubleValue());
        assertThat(result.getTotalTransactionsValue()).isEqualTo(currentTransactionAmount.doubleValue());
    }

    @Test
    public void shouldNotAllowNullParameters() {
        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        assertThatThrownBy(() -> spendLimitChecker.checkDurationLimit(null, newArrayList(), newArrayList(), currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("spendLimits");
        assertThatThrownBy(() -> spendLimitChecker.checkDurationLimit(newArrayList(), null, newArrayList(), currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("defaultSpendLimits");
        assertThatThrownBy(() -> spendLimitChecker.checkDurationLimit(newArrayList(), newArrayList(), null, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("erTransList");
        assertThatThrownBy(() -> spendLimitChecker.checkDurationLimit(newArrayList(), newArrayList(), newArrayList(), null, SpendLimitType.ACCOUNT_MONTH, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currentTransactionAmount");
        assertThatThrownBy(() -> spendLimitChecker.checkDurationLimit(newArrayList(), newArrayList(), newArrayList(), currentTransactionAmount, null, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("spendLimitType");
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

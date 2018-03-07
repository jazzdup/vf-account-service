package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;
import com.vodafone.charging.data.builder.SpendLimitDataBuilder;
import com.vodafone.charging.data.builder.SpendLimitDataProvider;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.data.ERTransactionDataBuilder.anErTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

/**
 * Tests the per Day account spend limits functionality
 */
public class SpendLimitCheckerDayLimitsTest extends SpendLimitCheckerBase {


    /*
    under dayLimit
     */
    @Test
    public void shouldNotBreachWhenDayLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimit() {
        //given
        //purchases total 16, current tx = 0.3 refunds 6.3 limit = 10
        final List<ERTransaction> transactions = SpendLimitDataProvider.anERTransactionListForCurrentDay();

        double expectedTxTotal = 10.00;
        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(todayDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY, 1);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getTotalTransactionsValue()).isEqualTo(expectedTxTotal);
        assertThat(result.getAppliedLimitValue()).isEqualTo(spendLimits.get(1).getLimit());
    }

    /*
    over dayLimit
    */
    @Test
    public void shouldBreachWhenDayLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimitCurrentTxMakesOverLimit() {
        //given
        //purchases total 16, current tx = 0.4 refunds 6.3 limit = 10
        final List<ERTransaction> transactions = SpendLimitDataProvider.anERTransactionListForCurrentDay();

        BigDecimal currentTransactionAmount = new BigDecimal(0.4);

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(todayDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY, 1);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_DAY);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_DAY.name());
        assertThat(result.getFailureReason()).doesNotContain("default");
        assertThat(result.getTotalTransactionsValue()).isEqualTo(10.1);
        assertThat(result.getAppliedLimitValue()).isEqualTo(10.0);

    }

    @Test
    public void shouldNotBreachDefaultWhenNoDayLimitDefinedAndPaymentsOverDefault() {

        //given
        //purchases total 16, current tx = 5.0 refunds 6.3 limit = 15
        final List<ERTransaction> transactions = SpendLimitDataProvider.anERTransactionListForCurrentDay();

        double expectedTxTotal = 14.7;
        BigDecimal currentTransactionAmount = new BigDecimal(5.0);

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(todayDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(Lists.emptyList(), defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY, 1);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getTotalTransactionsValue()).isEqualTo(expectedTxTotal);
        assertThat(result.getAppliedLimitValue()).isEqualTo(defaultSpendLimits.get(1).getLimit().doubleValue());
    }

    @Test
    public void shouldBreachDefaultWhenDayLimitNotDefinedAndPaymentsOver() {

        //given
        //purchases total 16, current tx = 6.0 refunds 6.3 limit = 15
        final List<ERTransaction> transactions = SpendLimitDataProvider.anERTransactionListForCurrentDay();

        BigDecimal currentTransactionAmount = new BigDecimal(6.00);
        double expectedTotalTx = 15.7;

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(todayDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(newArrayList(), defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY, 1);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_DAY);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_DAY.name());
        assertThat(result.getFailureReason()).contains("default spend limit");
        assertThat(result.getTotalTransactionsValue()).isEqualTo(expectedTotalTx);
        assertThat(result.getAppliedLimitValue()).isEqualTo(defaultSpendLimits.get(1).getLimit().doubleValue());
    }


    @Test
    public void shouldNotBreachDefaultWhenDayLimitDefinedAndPaymentsOverDefault() {
        //given
        //purchases total 16, current tx = 4.0 refunds 11.3 limit = 10
        final List<ERTransaction> transactions = SpendLimitDataProvider.anERTransactionListForCurrentDay();
        transactions.add(anErTransaction(new BigDecimal(5.0), todayDates.get("startDate").plusHours(1), ERTransactionType.REFUND));

        SpendLimit limit = SpendLimitDataBuilder.aSpendLimit(10, SpendLimitType.ACCOUNT_DAY);
        SpendLimit defaultLimit = SpendLimitDataBuilder.aSpendLimit(1, SpendLimitType.ACCOUNT_DAY);

        double expectedTxTotal = 8.7;
        BigDecimal currentTransactionAmount = new BigDecimal(4.0);

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(todayDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(newArrayList(limit), newArrayList(defaultLimit), transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY, 1);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getTotalTransactionsValue()).isEqualTo(expectedTxTotal);
        assertThat(result.getAppliedLimitValue()).isEqualTo(limit.getLimit().doubleValue());

    }

    @Test
    public void shouldNotBreachWhenNoLimitsDefined() {

        //given
        //purchases total 16, current tx = 5000.0 refunds 6.3 limit = none
        final List<ERTransaction> transactions = SpendLimitDataProvider.anERTransactionListForCurrentDay();
        BigDecimal currentTransactionAmount = new BigDecimal(5000.0);
        double expectedTxTotal = 5009.7;

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(todayDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(newArrayList(), newArrayList(), transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY, 1);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getTotalTransactionsValue()).isEqualTo(expectedTxTotal);
        assertThat(result.getAppliedLimitValue()).isEqualTo(0.0);

    }

    @Test
    public void shouldNotBreachWhenDayLimitDefinedAndNoDefaultLimit() {

        //given
        //purchases total 16, current tx = 0.11 refunds 6.3 limit = 10
        final List<ERTransaction> transactions = SpendLimitDataProvider.anERTransactionListForCurrentDay();
        BigDecimal currentTransactionAmount = new BigDecimal(0.11);
        double expectedTxTotal = 9.81;

        given(erDateCalculator.calculateSpendLimitDates(any(SpendLimitType.class), anyInt())).willReturn(todayDates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, newArrayList(), transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY, 1);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
        assertThat(result.getTotalTransactionsValue()).isEqualTo(expectedTxTotal);
        assertThat(result.getAppliedLimitValue()).isEqualTo(spendLimits.get(1).getLimit().doubleValue());

    }


}

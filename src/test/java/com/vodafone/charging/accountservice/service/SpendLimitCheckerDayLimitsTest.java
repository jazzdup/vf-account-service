package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.data.ERTransactionDataBuilder.anErTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;

/**
 * Tests the per Day account spend limits functionality
 */
public class SpendLimitCheckerDayLimitsTest extends SpendLimitCheckerBase {

    //DAY LIMITS TESTS

    /*
    under dayLimit
     */
    @Test
    public void shouldNotBreachWhenDayLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimit() {
        //given
        //purchases total 15.3, with refund reduces to 5.2 txAmount 0.3
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now(), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now(), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now(), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(10), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now(), ERTransactionType.REFUND),//include and subtract
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(2), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(3), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(4), ERTransactionType.REFUND));//exclude

        //expected calculations given data
        double previousTxAmount = 5.2;
        double prevousTxAmountPlusCurrentAmount = 5.5;

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(erDateCalculator.calculateBillingCycleDates(anyInt())).willReturn(dates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY, 1);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
    }

    /*
    under dayLimit
    */
    @Test
    public void shouldBreachWhenDayLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimitCurrentTxMakesOverLimit() {
        //given
        //purchases total 16, current tx = 0.3 refunds 6.3 limit = 10
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

        //expected calculations given data
        double previousTxAmount = 5.2;
        double prevousTxAmountPlusCurrentAmount = 5.5;

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);
        given(erDateCalculator.calculateBillingCycleDates(anyInt())).willReturn(dates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY, 1);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();

    }

    @Test
    public void shouldBreachDefaultWhenDayLimitNotDefinedButDefaultIs() {

        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(2), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(3), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(4), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(10), ERTransactionType.REFUND),//exclude
//                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusMinutes(10), ERTransactionType.REFUND),//include and subtract
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(2), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(3), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(4), ERTransactionType.REFUND));//exclude
        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(erDateCalculator.calculateBillingCycleDates(anyInt())).willReturn(dates);

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(newArrayList(), defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY, 1);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_DAY);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_DAY.name());
        assertThat(result.getFailureReason()).contains("default spend limit");

    }

    public void shouldBreachWhenDayLimitDefinedAndPaymentsOverLimitRefundsLowerTotalToBelowLimitThenCurrentTxOverLimit() {

        //Include

    }


}

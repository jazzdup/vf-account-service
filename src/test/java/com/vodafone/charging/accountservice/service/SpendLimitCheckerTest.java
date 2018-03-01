package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import com.vodafone.charging.accountservice.dto.SpendLimitResult;
import com.vodafone.charging.accountservice.dto.client.TransactionInfo;
import com.vodafone.charging.accountservice.dto.er.ERTransaction;
import com.vodafone.charging.accountservice.dto.er.ERTransactionType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.accountservice.domain.enums.SpendLimitType.*;
import static com.vodafone.charging.data.ERTransactionDateBuilder.anErTransaction;
import static com.vodafone.charging.data.builder.SpendLimitDataBuilder.aSpendLimit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class SpendLimitCheckerTest {

    @Mock
    private TimeZone timeZone;

    private List<SpendLimit> spendLimits;
    private List<SpendLimit> defaultSpendLimits;

    @InjectMocks
    private SpendLimitChecker spendLimitChecker;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));
    }

    @Before
    public void dataSetUp() {
        spendLimits = newArrayList(aSpendLimit(2.0, ACCOUNT_TX),
                aSpendLimit(10.0, ACCOUNT_DAY),
                aSpendLimit(50.0, ACCOUNT_MONTH));
        defaultSpendLimits = newArrayList(aSpendLimit(5.0, ACCOUNT_TX),
                aSpendLimit(15.0, ACCOUNT_DAY),
                aSpendLimit(55.0, ACCOUNT_MONTH));
    }


    @Test
    public void shouldNotBreachWhenAccountTxIsEqualToTxLimit() {

        //given
        final List<TransactionInfo> transactionInfo = newArrayList(TransactionInfo.builder().amount(new BigDecimal(2.0))
                .build());

        //when
        SpendLimitResult success = spendLimitChecker.checkTransactionLimit(spendLimits, defaultSpendLimits, transactionInfo);

        //then
        assertThat(success.isSuccess()).isTrue();

    }

    @Test
    public void shouldNotBreachWhenDayLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimit() {
        //given
        //purchases total 15.3, with refund reduces to 5.2
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(2), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(3), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(4), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(10), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusMinutes(10), ERTransactionType.REFUND),//include and subtract
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(2), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(3), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(4), ERTransactionType.REFUND));//exclude

        //expected calculations given data
        double previousTxAmount = 5.2;
        double prevousTxAmountPlusCurrentAmount = 5.5;

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
    }

    @Test
    public void shouldBreachWhenDayLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimitCurrentTxMakesOverLimit() {
        //given
        //purchases total 16, current tx = 0.3 refunds 6.3 limit = 10
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(5.0), LocalDateTime.now().minusHours(2), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.5), LocalDateTime.now().minusHours(3), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.5), LocalDateTime.now().minusHours(4), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(2), ERTransactionType.USAGE),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(10), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(6.3), LocalDateTime.now().minusMinutes(10), ERTransactionType.REFUND),//include and subtract
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(2), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(3), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(4), ERTransactionType.REFUND));//exclude

        //expected calculations given data
        double previousTxAmount = 5.2;
        double prevousTxAmountPlusCurrentAmount = 5.5;

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();

    }

    @Test
    public void shouldBreachWhenDayLimitDefinedPaymentsOverLimit() {

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

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_DAY);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_DAY.name());
        assertThat(result.getFailureReason()).doesNotContain("default spend limit");

    }

    @Test
    public void shouldBreachWhenDayLimitNotDefinedButDefaultIs() {

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

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(newArrayList(), defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_DAY);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_DAY);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_DAY.name());
        assertThat(result.getFailureReason()).contains("default spend limit");

    }

    @Test
    public void shouldNotBreachWhenMonthLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimit() {
        //given
        //purchases total 15.3, with refund reduces to 5.2
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(21.1), LocalDateTime.now().minusHours(2), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(22.1), LocalDateTime.now().withDayOfMonth(1), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(4), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), LocalDateTime.now().withDayOfMonth(2), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().withDayOfMonth(3), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(40), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(50), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(5.3), LocalDateTime.now().withDayOfMonth(5), ERTransactionType.REFUND),//include
                anErTransaction(new BigDecimal(10.5), LocalDateTime.now().withDayOfMonth(20), ERTransactionType.REFUND),//include
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(4), ERTransactionType.REFUND));//exclude
//payments=65.5 current Tx 0.3 refunds 15.8  (should calculate to 50.0) - spend limit = 50

        //expected calculations given data

        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH);

        //then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getFailureCauseType()).isNull();
        assertThat(result.getFailureReason()).isEmpty();
    }

    @Test
    public void shouldBreachWhenMonthLimitDefinedPaymentsOverLimitRefundsLowerTotalToBelowLimitThenCurrentTxOverLimit() {
        //given
        //purchases total 15.3, with refund reduces to 5.2
        final List<ERTransaction> transactions = newArrayList(
                anErTransaction(new BigDecimal(21.1), LocalDateTime.now().minusHours(2), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(22.1), LocalDateTime.now().withDayOfMonth(1), ERTransactionType.PURCHASE),//include
                anErTransaction(new BigDecimal(5.1), LocalDateTime.now().minusHours(4), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(7.1), LocalDateTime.now().withDayOfMonth(2), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().withDayOfMonth(3), ERTransactionType.USAGE),//include
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(40), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(10.1), LocalDateTime.now().minusDays(50), ERTransactionType.REFUND),//exclude
                anErTransaction(new BigDecimal(5.2), LocalDateTime.now().withDayOfMonth(5), ERTransactionType.REFUND),//include
                anErTransaction(new BigDecimal(10.5), LocalDateTime.now().withDayOfMonth(20), ERTransactionType.REFUND),//include
                anErTransaction(new BigDecimal(20.1), LocalDateTime.now().minusMonths(4), ERTransactionType.REFUND));//exclude
//payments=65.5 current Tx 0.3 refunds 15.7  (should calculate to 50.1) - spend limit = 50

        //expected calculations given data
        BigDecimal currentTransactionAmount = new BigDecimal(0.3);

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        //when
        final SpendLimitResult result =
                spendLimitChecker.checkDurationLimit(spendLimits, defaultSpendLimits, transactions, currentTransactionAmount, SpendLimitType.ACCOUNT_MONTH);

        //then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFailureCauseType()).isEqualTo(SpendLimitType.ACCOUNT_MONTH);
        assertThat(result.getFailureReason()).startsWith(SpendLimitType.ACCOUNT_MONTH.name());
        assertThat(result.getFailureReason()).doesNotContain("default spend limit");
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

    private static BigDecimal toScaledBigDecimal(double amount) {
        BigDecimal bdAmount = new BigDecimal(amount);
        bdAmount.setScale(2, RoundingMode.HALF_UP);
        return bdAmount;
    }


}

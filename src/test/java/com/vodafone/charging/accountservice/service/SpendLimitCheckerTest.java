package com.vodafone.charging.accountservice.service;

import com.google.common.collect.Maps;
import com.vodafone.charging.accountservice.TestBeanConfiguration;
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
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.accountservice.domain.enums.SpendLimitType.*;
import static com.vodafone.charging.data.ERTransactionDataBuilder.anErTransaction;
import static com.vodafone.charging.data.builder.SpendLimitDataBuilder.aSpendLimit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TestBeanConfiguration.class)
public class SpendLimitCheckerTest {

    @Mock
    private TimeZone timeZone;

    @Mock
    private ERDateCalculator erDateCalculator;

    private List<SpendLimit> spendLimits;
    private List<SpendLimit> defaultSpendLimits;
    private Map<String, LocalDateTime> dates = Maps.newHashMap();

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

        LocalDate now = LocalDate.now();
        LocalDateTime firstOfMonth = LocalDateTime.of(now, LocalTime.MIDNIGHT).withDayOfMonth(1);
        LocalDateTime endOfMonth = LocalDateTime.of(now, LocalTime.MAX);
        String startKey = "startDate";
        String endKey = "endDate";
        dates.put(startKey, firstOfMonth);
        dates.put(endKey, endOfMonth);
    }


    @Test
    public void shouldNotBreachWhenTxLimitDefinedAndAccountTxIsEqualToTxLimit() {
        //given
        final List<TransactionInfo> transactionInfo =
                newArrayList(TransactionInfo.builder().amount(new BigDecimal(2.0))
                        .build());

        //when
        SpendLimitResult success = spendLimitChecker.checkTransactionLimit(spendLimits, defaultSpendLimits, transactionInfo);

        //then
        assertThat(success.isSuccess()).isTrue();

    }

    public void shouldBreachWhenTxLimitDefinedAndAccountTxIsOverTxLimit() {
    }

    public void shouldNotBreachDefaultWhenNoTxLimitDefinedAndAccountTxUnderTxLimit() {
    }

    public void shouldBreachDefaultWhenNoTxLimitDefinedDefinedAndAccountTxIsOverTxLimit() {
    }

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

    //MONTHLY LIMITS

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
        given(erDateCalculator.calculateBillingCycleDates(anyInt())).willReturn(dates);

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
        given(erDateCalculator.calculateBillingCycleDates(anyInt())).willReturn(dates);

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

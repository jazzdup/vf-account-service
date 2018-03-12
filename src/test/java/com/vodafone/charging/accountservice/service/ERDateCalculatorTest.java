package com.vodafone.charging.accountservice.service;

import com.google.common.collect.Maps;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.TimeZone;

import static com.vodafone.charging.accountservice.service.ERDateCalculator.END_DATE_KEY;
import static com.vodafone.charging.accountservice.service.ERDateCalculator.START_DATE_KEY;
import static com.vodafone.charging.data.builder.AccountDataBuilder.anAccount;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ERDateCalculatorTest {

    @Mock
    private TimeZone timeZone;

    @InjectMocks
    private ERDateCalculator erDateCalculator;

    private LocalDateTime initTime = LocalDateTime.now();

    @Test
    public void shouldCalculateBillingCycleDatesFrom1stToEndOfCurrentMonth() {

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        final Map<String, LocalDateTime> dates =
                erDateCalculator.calculateBillingCycleDates(1);

        assertThat(dates.get(START_DATE_KEY))
                .isEqualByComparingTo(LocalDateTime.of(LocalDate.now().withDayOfMonth(1),
                        LocalTime.MIDNIGHT));

        assertThat(dates.get(END_DATE_KEY))
                .isEqualByComparingTo(LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
                        .with(TemporalAdjusters.lastDayOfMonth()));

    }

    /*
    if today is the 10th and billingCycleDay == 9 then should calculate 9th this month to 8th next month
    if today is the 10th and billingCycleDay == 20th then should calculate 20th last month to 20th this month
    start dates should be 00:00:00
    end dates should be 23:59:59:999
     */
    //Can't control when this test is run so have to cover the possibility that "yesterday" is sometimes last month
    // or this month
    @Test
    public void shouldCalculateBillingCycleDatesWithCycleDayBeforeTodayThisMonthOrLast() {
        LocalDateTime yesterday = initTime.minusDays(10);
        int billingCycleDay = yesterday.getDayOfMonth();

        final Map<String, LocalDateTime> expectedDates = getAccountMonthExpectedStartEndDates(billingCycleDay);
        final LocalDateTime expectedStart = expectedDates.get(ERDateCalculator.START_DATE_KEY);
        final LocalDateTime expectedEnd = expectedDates.get(ERDateCalculator.END_DATE_KEY);

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        final Map<String, LocalDateTime> dates =
                erDateCalculator.calculateBillingCycleDates(billingCycleDay);

        assertThat(dates.get(START_DATE_KEY))
                .isEqualByComparingTo(expectedStart);

        assertThat(dates.get(END_DATE_KEY))
                .isEqualByComparingTo(expectedEnd);
    }

    /*
    Technically billingCycleDay starts at midnight
     */
    @Test
    public void shouldCalculateBillingCycleDatesWithCycleDaySameAsTodayThisMonth() {

        int billingCycleDay = initTime.getDayOfMonth();

        LocalDateTime expectedStart;
        LocalDateTime expectedEnd;

        //set expectations
        expectedStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(billingCycleDay),
                LocalTime.MIDNIGHT);
        expectedEnd = LocalDateTime.of(LocalDate.now().plusMonths(1).withDayOfMonth(billingCycleDay - 1),
                LocalTime.MAX);

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        final Map<String, LocalDateTime> dates =
                erDateCalculator.calculateBillingCycleDates(billingCycleDay);

        assertThat(dates.get(START_DATE_KEY))
                .isEqualByComparingTo(expectedStart);

        assertThat(dates.get(END_DATE_KEY))
                .isEqualByComparingTo(expectedEnd);

    }

    @Test
    public void shouldCalculateAccountDayLimitDatesAndIgnoreBillingCycleDay() {

        final LocalDateTime expectedStartTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        final LocalDateTime expectedEndTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));
        final Map<String, LocalDateTime> dates =
                erDateCalculator.calculateDurationSpendLimitDates(SpendLimitType.ACCOUNT_DAY, 2000);

        assertThat(dates.get(START_DATE_KEY)).isEqualByComparingTo(expectedStartTime);
        assertThat(dates.get(END_DATE_KEY)).isEqualByComparingTo(expectedEndTime);

    }

    @Test
    public void shouldCalculateAccountMonthLimitDatesAndIgnoreInvalidBillingCycleDayWhenOver28() {
        final LocalDateTime expectedStartTime = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIDNIGHT);
        final LocalDateTime expectedEndTime = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));
        final Map<String, LocalDateTime> dates =
                erDateCalculator.calculateDurationSpendLimitDates(SpendLimitType.ACCOUNT_MONTH, 29);

        assertThat(dates.get(START_DATE_KEY)).isEqualByComparingTo(expectedStartTime);
        assertThat(dates.get(END_DATE_KEY)).isEqualByComparingTo(expectedEndTime);
    }

    @Test
    public void shouldCalculateAccountMonthLimitDatesAndIgnoreInvalidBillingCycleDayUnder1() {
        final LocalDateTime expectedStartTime = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIDNIGHT);
        final LocalDateTime expectedEndTime = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));
        final Map<String, LocalDateTime> dates =
                erDateCalculator.calculateDurationSpendLimitDates(SpendLimitType.ACCOUNT_MONTH, 0);

        assertThat(dates.get(START_DATE_KEY)).isEqualByComparingTo(expectedStartTime);
        assertThat(dates.get(END_DATE_KEY)).isEqualByComparingTo(expectedEndTime);
    }

    @Test
    public void shouldCalculateAccountMonthLimitDatesWithValidBillingCycleDay() {

        int billingCycleDay = 10;

        final Map<String, LocalDateTime> expectedDates = getAccountMonthExpectedStartEndDates(billingCycleDay);
        final LocalDateTime expectedStart = expectedDates.get(ERDateCalculator.START_DATE_KEY);
        final LocalDateTime expectedEnd = expectedDates.get(ERDateCalculator.END_DATE_KEY);

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));
        final Map<String, LocalDateTime> dates =
                erDateCalculator.calculateDurationSpendLimitDates(SpendLimitType.ACCOUNT_MONTH, billingCycleDay);

        assertThat(dates.get(START_DATE_KEY)).isEqualByComparingTo(expectedStart);
        assertThat(dates.get(END_DATE_KEY)).isEqualByComparingTo(expectedEnd);

    }

    @Test
    public void shouldGetEmptyResponseWhenAccountTxTypePassed() {
        assertThat(erDateCalculator.calculateDurationSpendLimitDates(SpendLimitType.ACCOUNT_TX, 10)).isEmpty();
    }

    @Test
    public void shouldGetStartDateCorrectly() {

        final Account account = anAccount(10, aChargingId());
        Map<String, LocalDateTime> expectedDates = getAccountMonthExpectedStartEndDates(account.getBillingCycleDay());

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        LocalDateTime startTime = erDateCalculator.calculateAccountBillingCycleDate(account);
        assertThat(startTime).isEqualByComparingTo(expectedDates.get(START_DATE_KEY));

    }

    @Test
    public void shouldValidateBillingCycleDay() {
        assertThat(ERDateCalculator.isValidBillingCycleDay(19)).isTrue();
        assertThat(ERDateCalculator.isValidBillingCycleDay(29)).isFalse();
        assertThat(ERDateCalculator.isValidBillingCycleDay(0)).isFalse();
        assertThat(ERDateCalculator.isValidBillingCycleDay(-10)).isFalse();
    }

    private Map<String, LocalDateTime> getAccountMonthExpectedStartEndDates(int billingCycleDay) {
        final LocalDateTime billingDate = LocalDateTime.of(LocalDate.now().withDayOfMonth(billingCycleDay), LocalTime.MIDNIGHT);

        Map<String, LocalDateTime> response = Maps.newHashMapWithExpectedSize(2);

        //set expectations
        if (billingDate.isBefore(LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIDNIGHT))) {
            //last month date so we want last month billingDate to this month billingDate
            response.put(ERDateCalculator.START_DATE_KEY, LocalDateTime.of(LocalDate.now().minusMonths(1).withDayOfMonth(billingCycleDay),
                    LocalTime.MIDNIGHT));
            response.put(ERDateCalculator.END_DATE_KEY, LocalDateTime.of(LocalDate.now().withDayOfMonth(billingCycleDay - 1), LocalTime.MAX));
        } else {
            //this month to next month
            response.put(ERDateCalculator.START_DATE_KEY, LocalDateTime.of(LocalDate.now().withDayOfMonth(billingCycleDay),
                    LocalTime.MIDNIGHT));
            response.put(ERDateCalculator.END_DATE_KEY,LocalDateTime.of(LocalDate.now().plusMonths(1).withDayOfMonth(billingCycleDay - 1), LocalTime.MAX));
        }

        return response;
    }

}

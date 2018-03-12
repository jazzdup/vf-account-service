package com.vodafone.charging.accountservice.service;

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

        assertThat(dates.get(ERDateCalculator.START_DATE_KEY))
                .isEqualByComparingTo(LocalDateTime.of(LocalDate.now().withDayOfMonth(1),
                        LocalTime.MIDNIGHT));

        assertThat(dates.get(ERDateCalculator.END_DATE_KEY))
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

        LocalDateTime expectedStart;
        LocalDateTime expectedEnd;

        //set expectations
        if (yesterday.isBefore(LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIDNIGHT))) {
            //last month date so we want last month billingDate to this month billingDate
            expectedStart = LocalDateTime.of(LocalDate.now().minusMonths(1).withDayOfMonth(billingCycleDay),
                    LocalTime.MIDNIGHT);
            expectedEnd = LocalDateTime.of(LocalDate.now().withDayOfMonth(billingCycleDay - 1), LocalTime.MAX);
        } else {
            //this month to next month
            expectedStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(billingCycleDay),
                    LocalTime.MIDNIGHT);
            expectedEnd = LocalDateTime.of(LocalDate.now().plusMonths(1).withDayOfMonth(billingCycleDay - 1), LocalTime.MAX);
        }

        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));

        final Map<String, LocalDateTime> dates =
                erDateCalculator.calculateBillingCycleDates(billingCycleDay);

        assertThat(dates.get(ERDateCalculator.START_DATE_KEY))
                .isEqualByComparingTo(expectedStart);

        assertThat(dates.get(ERDateCalculator.END_DATE_KEY))
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

        assertThat(dates.get(ERDateCalculator.START_DATE_KEY))
                .isEqualByComparingTo(expectedStart);

        assertThat(dates.get(ERDateCalculator.END_DATE_KEY))
                .isEqualByComparingTo(expectedEnd);

    }
//
//    @Test
//    public void  shouldCalculateSpendLimitDates()
}

package com.vodafone.charging.accountservice.service;

import com.google.common.collect.Maps;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import com.vodafone.charging.accountservice.domain.model.Account;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static java.util.Optional.ofNullable;

@Service
@Slf4j
public class ERDateCalculator {

    @Autowired
    private TimeZone timeZone;

    public static final String START_DATE_KEY = "startDate";
    public static final String END_DATE_KEY = "endDate";

    /**
     * If billingCycleDay == 1 then return the dateTime of the first day of the month at midnight
     * If billingCycleDay > 1 then return either that day of this month or the previous month.  This month if
     * we have passed it already, last month if not.
     */
    public Map<String, LocalDateTime> calculateBillingCycleDates(int billingCycleDay) {

        final LocalDateTime todayDateTime = LocalDateTime.now(timeZone.toZoneId());
        final LocalDate initDate = LocalDate.now(timeZone.toZoneId());
        Map<String, LocalDateTime> dates = new HashMap<>();

        //If 1 then we start at start of the current month
        if (billingCycleDay <= 1 || billingCycleDay > 28) {
            dates.put(START_DATE_KEY, LocalDateTime.of(initDate.withDayOfMonth(1), LocalTime.MIDNIGHT));
            dates.put(END_DATE_KEY, LocalDateTime.of(initDate.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX));
            return dates;
        }

        //If they have a cycle date, check if we have past it in the current month
        final LocalDateTime billingDateTimeThisMonth = LocalDateTime.of(initDate.withDayOfMonth(billingCycleDay).minusDays(1), LocalTime.MAX);
        if (todayDateTime.isAfter(billingDateTimeThisMonth)) {
            dates.put(START_DATE_KEY, LocalDateTime.of(initDate.withDayOfMonth(billingCycleDay), LocalTime.MIDNIGHT));//e.g. 6th this month
            dates.put(END_DATE_KEY, LocalDateTime.of(initDate.plusMonths(1).withDayOfMonth(billingCycleDay).minusDays(1), LocalTime.MAX)); //e.g. 5th next month
        } else {
            dates.put(START_DATE_KEY, LocalDateTime.of(initDate.minusMonths(1).withDayOfMonth(billingCycleDay), LocalTime.MIDNIGHT)); //e.g 6th last month
            dates.put(END_DATE_KEY, LocalDateTime.of(initDate.withDayOfMonth(billingCycleDay).minusDays(1), LocalTime.MAX)); //e.g. 5th this month
        }
        return dates;
    }

    /**
     * Depending on the type of SpendLimit passed in this works out the start and end date required.
     * Takes into account an optional billing cycle day for monthly spend limits only
     */
    public Map<String, LocalDateTime> calculateDurationSpendLimitDates(@NonNull final SpendLimitType spendLimitType,
                                                                       @Nullable final Integer billingCycleDay) {

        Map<String, LocalDateTime> dates = Maps.newHashMapWithExpectedSize(2);

        if (spendLimitType.equals(SpendLimitType.ACCOUNT_DAY)) {
            dates.put(START_DATE_KEY, LocalDateTime.of(LocalDate.now(timeZone.toZoneId()), LocalTime.MIDNIGHT));
            dates.put(END_DATE_KEY, LocalDateTime.of(LocalDate.now(timeZone.toZoneId()), LocalTime.MAX));
        } else if (spendLimitType.equals(SpendLimitType.ACCOUNT_MONTH)) {
            int startDayOfMonth = ofNullable(billingCycleDay).orElse(1);
            dates = calculateBillingCycleDates(startDayOfMonth);
        } else {
            log.warn("Expected a ACCOUNT_DAY or ACCOUNT_MONTH spend limit type but was " + spendLimitType.name());
        }
        return dates;
    }

    /**
     * Gives you the last billing cycle (start) day for an account as a LocalDateTime object.
     */
    public LocalDateTime calculateAccountBillingCycleDate(@NonNull Account account) {
        int billingCycleDay = ofNullable(account.getBillingCycleDay()).orElse(1);
        Map<String, LocalDateTime> dates = calculateBillingCycleDates(billingCycleDay);

        return dates.get(START_DATE_KEY);
    }

    /*
    billingCycleDay must be between 1-28 inclusive.
     */
    public static boolean isValidBillingCycleDay(@Nullable Integer billingCycleDay) {
        return (billingCycleDay != null && billingCycleDay > 0
                && billingCycleDay < 29);
    }

}

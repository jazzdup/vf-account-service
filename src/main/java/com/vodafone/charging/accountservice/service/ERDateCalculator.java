package com.vodafone.charging.accountservice.service;

import com.google.common.collect.Maps;
import com.vodafone.charging.accountservice.domain.enums.SpendLimitType;
import lombok.NonNull;
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
public class ERDateCalculator {

    @Autowired
    private TimeZone timeZone;

    public String getStartDateKey() {
        return "startDate";
    }

    public String getEndDateKey() {
        return "endDate";
    }

    /**
     * If billingCycleDay == 1 then return the dateTime of the first day of the month at midnight
     * If biillingCycleDay > 1 then return either that day of this month or the previous month.  This month if
     * we have passed it already, last month if not.
     */
    public Map<String, LocalDateTime> calculateBillingCycleDates(int billingCycleDay) {

        final LocalDate today = LocalDate.now(timeZone.toZoneId());
        Map<String, LocalDateTime> dates = new HashMap<>();

        //If 1 then we start at start of the current month
        if (billingCycleDay == 1) {
            dates.put(getStartDateKey(), LocalDateTime.of(today.withDayOfMonth(1), LocalTime.MIDNIGHT));
            dates.put(getEndDateKey(), LocalDateTime.of(today.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX));
            return dates;
        }

        //If they have a cycle date, check if we have past it in the current month
        LocalDate billingDateThisMonth = today.withDayOfMonth(billingCycleDay);
        if (billingDateThisMonth.isBefore(today)) {
            dates.put(getStartDateKey(), LocalDateTime.of(today.withDayOfMonth(billingCycleDay), LocalTime.MIDNIGHT));//e.g. 6th this month
            dates.put(getEndDateKey(), LocalDateTime.of(today.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX)); //e.g. 6th next month
        } else {
            dates.put(getStartDateKey(), LocalDateTime.of(today.minusMonths(1).withDayOfMonth(billingCycleDay), LocalTime.MIDNIGHT)); //e.g 6th last month
            dates.put(getEndDateKey(), LocalDateTime.of(today.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX)); //e.g. 6th this month
        }
        return dates;
    }

    /**
     * Depending on the type of SpendLimit passed in this works out the start and end date required.
     * Takes into account an optional billing cycle day for monthly spend limits only
     *
     * @param spendLimitType
     * @param billingCycleDay
     * @return
     */
    public Map<String, LocalDateTime> calculateSpendLimitDates(@NonNull final SpendLimitType spendLimitType,
                                                               @Nullable final Integer billingCycleDay) {

        Map<String, LocalDateTime> dates = Maps.newHashMapWithExpectedSize(2);

        if (spendLimitType.equals(SpendLimitType.ACCOUNT_DAY)) {
            dates.put(getStartDateKey(), LocalDateTime.of(LocalDate.now(timeZone.toZoneId()), LocalTime.MIDNIGHT));
            dates.put(getEndDateKey(), LocalDateTime.of(LocalDate.now(timeZone.toZoneId()), LocalTime.MAX));
        } else if (spendLimitType.equals(SpendLimitType.ACCOUNT_MONTH)) {
            int startDayOfMonth = ofNullable(billingCycleDay).orElse(1);
            dates = calculateBillingCycleDates(startDayOfMonth);
        }
        return dates;
    }

}

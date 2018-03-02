package com.vodafone.charging.accountservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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
        final String startKey = "startDate";
        final String endKey = "endDate";

        //If 1 then we start at start of the current month
        if (billingCycleDay == 1) {
            dates.put(startKey, LocalDateTime.of(today.withDayOfMonth(1), LocalTime.MIDNIGHT));
            dates.put(endKey, LocalDateTime.of(today.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX));
            return dates;
        }

        //If they have a cycle date, check if we have past it in the current month
        LocalDate billingDateThisMonth = today.withDayOfMonth(billingCycleDay);
        if (billingDateThisMonth.isBefore(today)) {
            dates.put(startKey, LocalDateTime.of(today.withDayOfMonth(billingCycleDay), LocalTime.MIDNIGHT));//e.g. 6th this month
            dates.put(endKey, LocalDateTime.of(today.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX)); //e.g. 6th next month
        } else {
            dates.put(startKey, LocalDateTime.of(today.minusMonths(1).withDayOfMonth(billingCycleDay), LocalTime.MIDNIGHT)); //e.g 6th last month
            dates.put(endKey, LocalDateTime.of(today.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX)); //e.g. 6th this month
        }
        return dates;
    }

}

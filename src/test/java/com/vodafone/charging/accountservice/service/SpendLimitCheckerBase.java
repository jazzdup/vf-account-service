package com.vodafone.charging.accountservice.service;

import com.google.common.collect.Maps;
import com.vodafone.charging.accountservice.domain.model.SpendLimit;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.google.common.collect.Lists.newArrayList;
import static com.vodafone.charging.accountservice.domain.enums.SpendLimitType.*;
import static com.vodafone.charging.data.builder.SpendLimitDataBuilder.aSpendLimit;
import static org.mockito.BDDMockito.given;

public class SpendLimitCheckerBase {

    @Mock
    TimeZone timeZone;

    @Mock
    ERDateCalculator erDateCalculator;

    List<SpendLimit> spendLimits;
    List<SpendLimit> defaultSpendLimits;
    Map<String, LocalDateTime> monthDates = Maps.newHashMap();
    Map<String, LocalDateTime> todayDates = Maps.newHashMap();

    @InjectMocks
    SpendLimitChecker spendLimitChecker;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        given(timeZone.toZoneId()).willReturn(ZoneId.of("CET"));
        given(erDateCalculator.getStartDateKey()).willCallRealMethod();
        given(erDateCalculator.getEndDateKey()).willCallRealMethod();
    }

    @Before
    public void dataSetUp() {
        spendLimits = newArrayList(aSpendLimit(2.0, ACCOUNT_TX),
                aSpendLimit(10.0, ACCOUNT_DAY),
                aSpendLimit(50.0, ACCOUNT_MONTH));
        defaultSpendLimits = newArrayList(aSpendLimit(5.0, ACCOUNT_TX),
                aSpendLimit(15.0, ACCOUNT_DAY),
                aSpendLimit(55.0, ACCOUNT_MONTH));

        //For testing these are always from 1st to end of current month
        LocalDate now = LocalDate.now();
        LocalDateTime firstOfMonth = LocalDateTime.of(now, LocalTime.MIDNIGHT).withDayOfMonth(1);
        LocalDateTime endOfMonth = LocalDateTime.of(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX);
        String startKey = "startDate";
        String endKey = "endDate";
        monthDates.put(startKey, firstOfMonth);
        monthDates.put(endKey, endOfMonth);

        LocalDateTime startOfDay = now.atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(now, LocalTime.MAX);

        todayDates.put(startKey, startOfDay);
        todayDates.put(endKey, endOfDay);

    }


}

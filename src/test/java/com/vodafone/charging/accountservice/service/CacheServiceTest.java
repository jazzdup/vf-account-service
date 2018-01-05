package com.vodafone.charging.accountservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class CacheServiceTest {

    @InjectMocks
    private CacheService cacheService;

    @Test
    public void shouldRetrieveCachedAccountInfo() {
        assertThatThrownBy(() -> cacheService.retrieveSummaryAccountInfo())
                .isInstanceOf(UnsupportedOperationException.class);
    }
    @Test
    public void shouldSaveCachedAccountInfo() {
        assertThatThrownBy(() -> cacheService.saveSummaryAccountInfo(aEnrichedAccountInfo()))
                .isInstanceOf(UnsupportedOperationException.class);
    }




}

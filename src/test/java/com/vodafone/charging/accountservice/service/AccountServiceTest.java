package com.vodafone.charging.accountservice.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Locale;

import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(AccountService.class);
    }

    @Test
    public void shouldCallIFServiceAndCachingService() {
        assertThatThrownBy(() -> accountService.enrichAccountData
                (aContextData("contextName", Locale.UK, aChargingId())))
                .isInstanceOf(UnsupportedOperationException.class);
    }

}

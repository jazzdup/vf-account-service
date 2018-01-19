package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    @Mock
    private ERIFClient erifClient;

    @InjectMocks
    private AccountService accountService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCallIFServiceAndCachingService() throws Exception {
        //given
        final EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo();
        given(erifClient.validate(any(), any()))
                .willReturn(expectedInfo);
        //when
        EnrichedAccountInfo info = accountService.enrichAccountData(aContextData());
        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(info);

    }

}

package com.vodafone.charging.accountservice.service;

import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.data.message.JsonConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private JsonConverter converter;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(ERIFClient.class);
    }

    @Test
    public void shouldCallIFServiceAndCachingService() throws Exception {
        //given
        final EnrichedAccountInfo expectedInfo = aEnrichedAccountInfo();

//        given(accountService.enrichAccountData(any()))
//                .willReturn(expectedInfo);

        given(erifClient.validate(any(), any()))
                .willReturn(expectedInfo);

        EnrichedAccountInfo info = accountService.enrichAccountData(aContextData());
        assertThat(expectedInfo).isEqualToComparingFieldByField(info);


//        assertThatThrownBy(() -> accountService.enrichAccountData
//                (aContextData("contextName", Locale.UK, aChargingId())))
//                .isInstanceOf(UnsupportedOperationException.class);
    }

}

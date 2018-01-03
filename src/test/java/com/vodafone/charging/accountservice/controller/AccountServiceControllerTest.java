package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Locale;

import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountServiceController accountServiceController;

    @Before()
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnOk() {
        //given
        final EnrichedAccountInfo expectedAccountInfo = aEnrichedAccountInfo();
        final ContextData contextData = aContextData();

        given(accountService.enrichAccountData(contextData)).willReturn(expectedAccountInfo);

        //when
        final ResponseEntity<EnrichedAccountInfo> enrichedAccountInfoResponse =
                accountServiceController.enrichAccountData(contextData);

        //then
        assertThat(ResponseEntity.ok(expectedAccountInfo))
                .isEqualToIgnoringGivenFields(enrichedAccountInfoResponse, "body");
        assertThat(expectedAccountInfo).isEqualToComparingFieldByField(enrichedAccountInfoResponse.getBody());
    }

    @Test
    public void shouldReturnHttpBadRequest() {
        final ResponseEntity<EnrichedAccountInfo> enrichedAccountInfoResponse =
                accountServiceController.enrichAccountData(null);
        assertThat(enrichedAccountInfoResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenNullContextName() {
        assertThatThrownBy(() -> accountServiceController.checkContextData(aContextData(null, Locale.UK, aChargingId())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("contextName");

    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenNullLocale() {
        assertThatThrownBy(() -> accountServiceController.checkContextData(aContextData(
                        "test-context-name",
                        null,
                        aChargingId())));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenNullChargingId() {
        assertThatThrownBy(() -> accountServiceController.checkContextData(aContextData(
                "test-context-name",
                Locale.UK,
                null)));
    }

}

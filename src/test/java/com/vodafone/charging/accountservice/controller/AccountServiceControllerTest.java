package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.exception.ApplicationLogicException;
import com.vodafone.charging.accountservice.exception.MethodArgumentValidationException;
import com.vodafone.charging.accountservice.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Locale;
import java.util.Random;

import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aNullableChargingId;
import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static com.vodafone.charging.data.builder.HttpHeadersDataBuilder.aHttpHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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
    public void shouldPassCorrectDataAndReturnOkWhenCorrectDataIsReceived() {
        ArgumentCaptor<ContextData> captor = ArgumentCaptor.forClass(ContextData.class);
        //given
        final EnrichedAccountInfo expectedAccountInfo = aEnrichedAccountInfo();
        final ContextData contextData = aContextData();
        final HttpHeaders headers = aHttpHeaders(contextData.getClientId(),
                contextData.getLocale(),
                contextData.getChargingId());

        given(accountService.enrichAccountData(contextData)).willReturn(expectedAccountInfo);

        //when
        final ResponseEntity<EnrichedAccountInfo> enrichedAccountInfoResponse =
                accountServiceController.enrichAccountData(headers, contextData);

        //then
        verify(accountService).enrichAccountData(captor.capture());
        ContextData arg = captor.getValue();
        assertThat(arg).isEqualToComparingFieldByField(contextData);

        assertThat(ResponseEntity.ok(expectedAccountInfo))
                .isEqualToIgnoringGivenFields(enrichedAccountInfoResponse, "body");
        assertThat(expectedAccountInfo).isEqualToComparingFieldByField(enrichedAccountInfoResponse.getBody());
    }

    @Test
    public void shouldThrowMethodArgumentValidationExceptionWhenNullChargingIdValueIsNull() {
        final ContextData contextData = aContextData("test-context-name", Locale.UK, aChargingId(null));
        assertThatThrownBy(() -> accountServiceController.checkContextData(contextData))
                .isInstanceOf(MethodArgumentValidationException.class)
                .isNotInstanceOf(IllegalArgumentException.class)
                .hasMessage("chargingId.value is compulsory but was empty");
        verifyZeroInteractions(accountService);
    }

    @Test
    public void shouldThrowMethodArgumentValidationExceptionWhenNullChargingIdValueIsEmpty() {
        final ContextData contextData = aContextData("test-context-name", Locale.UK, aChargingId(""));
        assertThatThrownBy(() -> accountServiceController.checkContextData(contextData))
                .isInstanceOf(MethodArgumentValidationException.class)
                .isNotInstanceOf(IllegalArgumentException.class)
                .hasMessage("chargingId.value is compulsory but was empty");
        verifyZeroInteractions(accountService);
    }

    @Test
    public void shouldThrowMethodArgumentValidationExceptionWhenNullChargingIdTypeIsNull() {

        ChargingId chargingId = aNullableChargingId(null, String.valueOf(new Random().nextInt()));

        final ContextData contextData = aContextData("test-context-name", Locale.UK, chargingId);
        assertThatThrownBy(() -> accountServiceController.checkContextData(contextData))
                .isInstanceOf(MethodArgumentValidationException.class)
                .isNotInstanceOf(IllegalArgumentException.class)
                .hasMessage("chargingId.type is compulsory but was empty");
        verifyZeroInteractions(accountService);
    }

    @Test
    public void shouldWrapExceptionIntoApplicationLogicException() {
        final ContextData contextData = aContextData();
        final HttpHeaders headers = aHttpHeaders(contextData.getClientId(),
                contextData.getLocale(),
                contextData.getChargingId());

        final String message = "This is a test exception";
        given(accountService.enrichAccountData(contextData))
                .willThrow(new NullPointerException(message));

        assertThatThrownBy(() -> accountServiceController.enrichAccountData(headers, contextData))
                .isInstanceOf(ApplicationLogicException.class)
                .hasMessage(message)
                .hasCauseInstanceOf(NullPointerException.class);
    }

//    TODO HEADER TESTS PROBABLY NOT REQUIRED ANY MORE

//    @Test
//    public void shouldThrowMethodHeaderValidationExceptionWhenCountryIsMissing() {
//        HttpHeaders headers = mock(HttpHeaders.class);
//        given(headers.get(COUNTRY_HEADER_NAME)).willReturn(newArrayList());
//
//        assertThatThrownBy(() -> accountServiceController.checkMandatoryHeaders(headers))
//                .isInstanceOf(MethodArgumentValidationException.class)
//                .hasCauseInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("header: " + COUNTRY_HEADER_NAME + " is mandatory");
//    }
//    @Test
//    public void shouldThrowMethodHeaderValidationExceptionWhenCountryIsEmptyString() {
//        HttpHeaders headers = mock(HttpHeaders.class);
//        given(headers.get(COUNTRY_HEADER_NAME)).willReturn(newArrayList(""));
//        given(headers.get(TARGET_HEADER_NAME)).willReturn(newArrayList("local"));
//
//        assertThatThrownBy(() -> accountServiceController.checkMandatoryHeaders(headers))
//                .isInstanceOf(MethodArgumentValidationException.class)
//                .hasCauseInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("header: " + COUNTRY_HEADER_NAME + " is mandatory");
//    }
//
//    @Test
//    public void shouldThrowMethodHeaderValidationExceptionWhenCountryIsNull() {
//        HttpHeaders headers = mock(HttpHeaders.class);
//        given(headers.get(COUNTRY_HEADER_NAME)).willReturn(null);
//        given(headers.get(TARGET_HEADER_NAME)).willReturn(newArrayList("local"));
//
//        assertThatThrownBy(() -> accountServiceController.checkMandatoryHeaders(headers))
//                .isInstanceOf(MethodArgumentValidationException.class)
//                .hasCauseInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("header: " + COUNTRY_HEADER_NAME + " is mandatory");
//    }
//
//    @Test
//    public void shouldThrowMethodHeaderValidationExceptionWhenTargetIsMissing() {
//        HttpHeaders headers = mock(HttpHeaders.class);
//        given(headers.get(COUNTRY_HEADER_NAME)).willReturn(newArrayList("GB"));
//        given(headers.get(TARGET_HEADER_NAME)).willReturn(newArrayList());
//
//        assertThatThrownBy(() -> accountServiceController.checkMandatoryHeaders(headers))
//                .isInstanceOf(MethodArgumentValidationException.class)
//                .hasCauseInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("header: " + TARGET_HEADER_NAME + " is mandatory");
//    }
//
//    @Test
//    public void shouldThrowMethodHeaderValidationExceptionWhenTargetIsEmptyString() {
//        HttpHeaders headers = mock(HttpHeaders.class);
//        given(headers.get(COUNTRY_HEADER_NAME)).willReturn(newArrayList("GB"));
//        given(headers.get(TARGET_HEADER_NAME)).willReturn(newArrayList(""));
//
//        assertThatThrownBy(() -> accountServiceController.checkMandatoryHeaders(headers))
//                .isInstanceOf(MethodArgumentValidationException.class)
//                .hasCauseInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("header: " + TARGET_HEADER_NAME + " is mandatory");
//    }
//
//    @Test
//    public void shouldThrowMethodHeaderValidationExceptionWhenTargetIsNull() {
//        HttpHeaders headers = mock(HttpHeaders.class);
//        given(headers.get(COUNTRY_HEADER_NAME)).willReturn(newArrayList("GB"));
//        given(headers.get(TARGET_HEADER_NAME)).willReturn(null);
//
//        assertThatThrownBy(() -> accountServiceController.checkMandatoryHeaders(headers))
//                .isInstanceOf(MethodArgumentValidationException.class)
//                .hasCauseInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("header: " + TARGET_HEADER_NAME + " is mandatory");
//    }

}

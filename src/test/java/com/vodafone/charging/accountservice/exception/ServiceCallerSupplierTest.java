package com.vodafone.charging.accountservice.exception;

import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Supplier;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;
import static com.vodafone.charging.data.builder.EnrichedAccountInfoDataBuilder.aEnrichedAccountInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ServiceCallerSupplierTest {

    @InjectMocks
    private ServiceCallerSupplier serviceCallerSupplier;

    @Mock
    private AccountService accountService;

    @Test
    public void shouldReturnSupplierWithCorrectType() {
        //given
        final ContextData contextData = aContextData();
        EnrichedAccountInfo expected = aEnrichedAccountInfo();
        given(accountService.enrichAccountData(contextData)).willReturn(expected);

        //when
        final Supplier<EnrichedAccountInfo> accountInfo =
                serviceCallerSupplier.wrap(() -> accountService.enrichAccountData(contextData));
        //then
        assertThat(accountInfo.get()).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void shouldWrapRuntimeExceptionToApplicationLogicException() {
        final String message = "This is a test Exception" + this.getClass().hashCode();
        checkResult(ApplicationLogicException.class, new RuntimeException(message));
    }

    @Test
    public void shouldWrapNullPointerExceptionToApplicationLogicException() {
        final String message = "This is a test Exception" + this.getClass().hashCode();
        checkResult(ApplicationLogicException.class, new NullPointerException(message));
    }

    @Test
    public void shouldNotWrapRepositoryResourceNotFound() {
        final String message = "This is a test Exception" + this.getClass().hashCode();
        checkUpwrappedException(new RepositoryResourceNotFoundException(message));
    }

    @Test
    public void shouldNotWrapApplicationLogicException() {
        final String message = "This is a test Exception" + this.getClass().hashCode();
        checkUpwrappedException(new ApplicationLogicException(message));
    }

    private <T extends Exception> void checkResult(Class<T> expectedType, Exception expectedCause) {
        final ContextData contextData = aContextData();
        given(accountService.enrichAccountData(contextData)).willThrow(expectedCause);

        assertThatThrownBy(serviceCallerSupplier.wrap(() -> accountService.enrichAccountData(contextData))::get)
                .isInstanceOf(expectedType)
                .hasMessage(expectedCause.getMessage())
                .hasCauseInstanceOf(expectedCause.getClass());
    }

    private <T extends Exception> void checkUpwrappedException(Exception expectedType) {
        final ContextData contextData = aContextData();
        given(accountService.enrichAccountData(contextData)).willThrow(expectedType);

        assertThatThrownBy(serviceCallerSupplier.wrap(() -> accountService.enrichAccountData(contextData))::get)
                .isInstanceOf(expectedType.getClass())
                .hasMessage(expectedType.getMessage())
                .hasNoCause();

    }

}

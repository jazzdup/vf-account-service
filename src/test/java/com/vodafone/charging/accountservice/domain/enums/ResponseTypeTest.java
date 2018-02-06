package com.vodafone.charging.accountservice.domain.enums;

import com.vodafone.charging.accountservice.domain.ERIFResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.vodafone.charging.accountservice.domain.enums.ResponseStatus.*;
import static com.vodafone.charging.accountservice.domain.enums.ResponseType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

@RunWith(MockitoJUnitRunner.class)
public class ResponseTypeTest {


    @Test
    public void shouldCreateResponseTypes() {

        assertThat(ResponseType.OK.getStatus()).isEqualTo(ACCEPTED);
        assertThat(ResponseType.OK.getID()).isEqualTo(ACCEPTED.getId());
        assertThat(ResponseType.OK.getName()).isEqualTo("OK");

        assertThat(CONTENT_BLOCKED.getStatus()).isEqualTo(REJECTED);
        assertThat(USER_SPEND_LIMIT.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.SPEND_LIMIT.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.INSUFFICIENT_FUNDS.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.USER_SUSPENDED.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.AMOUNT_INVALID.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.REJECTED_OTHER.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.USER_NOT_FOUND.getStatus()).isEqualTo(DENIED);
        assertThat(ResponseType.USER_INVALID.getStatus()).isEqualTo(DENIED);
        assertThat(ResponseType.USER_BARRED.getStatus()).isEqualTo(DENIED);
        assertThat(ResponseType.USER_INVALID_LOCALE.getStatus()).isEqualTo(DENIED);
        assertThat(ResponseType.DENIED_OTHER.getStatus()).isEqualTo(DENIED);
        assertThat(ResponseType.VALIDATION_ERROR.getStatus()).isEqualTo(ERROR);
        assertThat(ResponseType.SYSTEM_ERROR.getStatus()).isEqualTo(ERROR);
        assertThat(ResponseType.USER_INVALID_ACCOUNT_TYPE.getStatus()).isEqualTo(ERROR);
        assertThat(ResponseType.INVALID_PAYMENT_ID.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.INVALID_PSPREFERENCE.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.CARD_ERROR.getStatus()).isEqualTo(DENIED);
        assertThat(ResponseType.CARD_FRAUD.getStatus()).isEqualTo(DENIED);
        assertThat(ResponseType.USER_BLOCKED.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.KYC_NOT_VERIFIED.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.KYC_IN_PROGRESS.getStatus()).isEqualTo(REJECTED);
        assertThat(ResponseType.KYC_REJECTED.getStatus()).isEqualTo(REJECTED);
    }

    @Test
    //Not checking all since this is imported code
    public void shouldCheckIfValidCombination() {
        assertThat(ResponseType.isValidCombination(ACCEPTED.getName(), "OK")).isTrue();
        assertThat(ResponseType.isValidCombination(REJECTED.getName(), CONTENT_BLOCKED.getName())).isTrue();
        assertThat(ResponseType.isValidCombination(REJECTED.getName(), USER_SPEND_LIMIT.getName())).isTrue();
        assertThat(ResponseType.isValidCombination(DENIED.getName(), USER_NOT_FOUND.getName())).isTrue();
        assertThat(ResponseType.isValidCombination(DENIED.getName(), "GIBBERISH")).isFalse();

    }

    @Test
    //Not checking all since this is imported code
    public void shouldCheckIfValidResponse() {
        ERIFResponse response = mock(ERIFResponse.class);
        given(response.getStatus()).willReturn(ResponseStatus.REJECTED.getName());
        given(response.getErrId()).willReturn(USER_SPEND_LIMIT.getName());
        assertThat(ResponseType.isValidResponse(response)).isTrue();
        response = mock(ERIFResponse.class);

        reset(response);

        given(response.getStatus()).willReturn(ResponseStatus.DENIED.getName());
        given(response.getErrId()).willReturn(USER_SPEND_LIMIT.getName());
        assertThat(ResponseType.isValidResponse(response)).isFalse();
    }

    @Test
    public void shouldCorrectGetResponseStatus() {
        assertThat(ResponseType.getStatusStr("OK")).isEqualTo(ResponseStatus.ACCEPTED.getName());
    }

    @Test
    public void shouldReturnEmptyResponseStatusString() {
        assertThat(ResponseType.getStatusStr("GIBBERISH")).isEmpty();
    }

}

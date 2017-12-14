package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.exception.BadRequestException;
import com.vodafone.charging.accountservice.domain.EnrichedAccountData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static com.vodafone.charging.data.builder.AccountSummaryDataBuilder.aAccount;
import static com.vodafone.charging.data.builder.AccountSummaryDataBuilder.aAccountWithNullAccountId;
import static com.vodafone.charging.data.builder.ValidationDataBuilder.aValidation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceControllerTest {

    @InjectMocks
    private AccountServiceController accountServiceController;

    @Before()
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnOk() {
        ResponseEntity<EnrichedAccountData> validationResponse = accountServiceController
                .enrichAccountData(aAccount());

        assertThat(ResponseEntity.ok(aValidation()))
                .isEqualToIgnoringGivenFields(validationResponse, "body");
        assertThat(aValidation()).isEqualToIgnoringGivenFields(validationResponse.getBody(),
                "id");
    }

    @Test(expected = BadRequestException.class)
    public void shouldHandleNullBody() {
        accountServiceController.enrichAccountData(null);
    }

    @Test(expected = BadRequestException.class)
    public void shouldHandleNullValue() {
        accountServiceController.enrichAccountData(aAccountWithNullAccountId());
    }
}

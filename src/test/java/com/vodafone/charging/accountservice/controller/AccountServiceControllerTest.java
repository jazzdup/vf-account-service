package com.vodafone.charging.accountservice.controller;

import com.vodafone.charging.accountservice.exception.BadRequestException;
import com.vodafone.charging.accountservice.domain.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static com.vodafone.charging.data.builder.AccountDataBuilder.aAccount;
import static com.vodafone.charging.data.builder.AccountDataBuilder.aAccountWithNullAccountId;
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
        ResponseEntity<Validation> validationResponse = accountServiceController
                .validate(aAccount());

        assertThat(ResponseEntity.ok(aValidation()))
                .isEqualToIgnoringGivenFields(validationResponse, "body");
        assertThat(aValidation()).isEqualToIgnoringGivenFields(validationResponse.getBody(),
                "id");
    }

    @Test(expected = BadRequestException.class)
    public void shouldHandleNullBody() {
        accountServiceController.validate(null);
    }

    @Test(expected = BadRequestException.class)
    public void shouldHandleNullValue() {
        accountServiceController.validate(aAccountWithNullAccountId());
    }
}

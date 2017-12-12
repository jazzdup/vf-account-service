package com.vodafone.charging.accountservice.controller;

import com.google.common.collect.Lists;
import com.vodafone.charging.accountservice.model.Account;
import com.vodafone.charging.accountservice.model.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Locale;
import java.util.Random;

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

        String accountId = String.valueOf(new Random().nextInt());

        ResponseEntity<Validation> result = accountServiceController
                .validate(accountId,
                        new Account.Builder()
                        .locale(Locale.UK)
                        .accountId(new Random().nextInt() + "")
                        .build());

        assertThat(ResponseEntity.ok(getValidation()))
                .isEqualToIgnoringGivenFields(result, "body") ;
        assertThat(getValidation()).isEqualToIgnoringGivenFields(result.getBody(),
                "id");
    }

    private Validation getValidation() {
        return new Validation.Builder().usergroups(Lists.newArrayList("test-usergroup")).result(true).build();
    }


}

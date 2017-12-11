package com.vodafone.charging.accountservice.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountServiceController accountServiceController;

    @Before()
    public void setUp() {
        initMocks(this);
    }


    @Test
    public void shouldReturnOk() {

        assertThat(ResponseEntity.ok().build())
                .isEqualToComparingFieldByField(accountServiceController.validate("123"));

    }

}

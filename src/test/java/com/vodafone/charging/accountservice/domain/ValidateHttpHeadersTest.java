package com.vodafone.charging.accountservice.domain;

import com.vodafone.charging.validator.HttpHeaderValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.vodafone.charging.data.builder.ContextDataDataBuilder.aContextData;

@RunWith(MockitoJUnitRunner.class)
public class ValidateHttpHeadersTest {

    @Test
    public void shouldCreateAllValidateHeadersCorrectly() {
        ContextData contextData = aContextData();
        ValidateHttpHeaders validateHttpHeaders = new ValidateHttpHeaders(contextData);
        HttpHeaderValidator.validateHttpHeadersJson(validateHttpHeaders.getHttpHeaders(), contextData);
    }
}

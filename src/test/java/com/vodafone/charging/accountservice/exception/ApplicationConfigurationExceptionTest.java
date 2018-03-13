package com.vodafone.charging.accountservice.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationConfigurationExceptionTest {

    @Test
    public void createExceptionWithNoCause() {
        final String message = "This is a " + this.hashCode();

        assertThat(new ApplicationConfigurationException(message))
                .isInstanceOf(ApplicationConfigurationException.class)
                .hasMessageContaining(message);

        assertThat(new ApplicationConfigurationException((message), new RuntimeException()))
                .isInstanceOf(ApplicationConfigurationException.class)
                .hasMessageContaining(message)
                .hasCauseInstanceOf(RuntimeException.class);
    }

}

package com.vodafone.charging.accountservice.exception;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorIdsTest {


    @Test
    public void shouldGetAllErrorIds() {
        String errorId = "VAS Internal Server Error";
        String desc = "Internal Server Error in Vodafone Account Service";
        assertThat(ErrorIds.VAS_INTERNAL_SERVER_ERROR.errorId())
                .isEqualTo(errorId);
        assertThat(ErrorIds.VAS_INTERNAL_SERVER_ERROR.errorDescription())
                .isEqualTo(desc);

    }

}

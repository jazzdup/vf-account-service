package com.vodafone.charging.accountservice.domain.enums;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class ResponseStatusTest {

    @Test
    public void shouldCreateResponseStatus() {

        ResponseStatus status = ResponseStatus.get("ACCEPTED", 1);
        assertThat(status).isNotNull();
        status = ResponseStatus.get("ERROR", 4);
        assertThat(status).isNotNull();
        status = ResponseStatus.get("DENIED", 0);
        assertThat(status).isNotNull();
        status = ResponseStatus.get("REJECTED", 2);
        assertThat(status).isNotNull();

    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenNothingMatches() {

        String message = "are not a valid response status";
        assertThatThrownBy(() -> ResponseStatus.get("ACCEPTED", 55))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ACCEPTED")
                .hasMessageContaining(message);
        assertThatThrownBy(() -> ResponseStatus.get("ERROR", 33))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ERROR")
                .hasMessageContaining(message);
        assertThatThrownBy(() -> ResponseStatus.get("DENIED", 22))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DENIED")
                .hasMessageContaining(message);
        assertThatThrownBy(() -> ResponseStatus.get("REJECTED", 22))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("REJECTED")
                .hasMessageContaining(message);
        assertThatThrownBy(() -> ResponseStatus.get(ResponseStatus.INVALID_BAN.getName(), 22))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("INVALID_BAN")
                .hasMessageContaining(message);

    }

    @Test
    public void shouldReturnTrueWhenCorrectStringPassed() {
        assertThat(ResponseStatus.isAccepted("ACCEPTED")).isTrue();
        assertThat(ResponseStatus.isError("ERROR")).isTrue();
        assertThat(ResponseStatus.isDenied("DENIED")).isTrue();
        assertThat(ResponseStatus.isRejected("REJECTED")).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenCorrectStringNotPassed() {
        assertThat(ResponseStatus.isAccepted("ACCEPTE")).isFalse();
        assertThat(ResponseStatus.isError("ERRO")).isFalse();
        assertThat(ResponseStatus.isDenied("DENED")).isFalse();
        assertThat(ResponseStatus.isRejected("RJECTED")).isFalse();
    }

    @Test
    public void shouldCheckVariousStatusCorrectly() {
        assertThat(ResponseStatus.isFailed("ERROR")).isTrue();
        assertThat(ResponseStatus.isFailed("ACCEPTED")).isFalse();
        assertThat(ResponseStatus.isFailed("ACEPTED")).isFalse();
        assertThat(ResponseStatus.REJECTED.isFailed()).isTrue();
        assertThat(ResponseStatus.ACCEPTED.isFailed()).isFalse();
        assertThat(ResponseStatus.isValid("ACCEPTED")).isTrue();
        assertThat(ResponseStatus.isValid("ERROR")).isTrue();
        assertThat(ResponseStatus.isValid("ERR")).isFalse();
        assertThat(ResponseStatus.isValid(ResponseStatus.INVALID_BAN.getName())).isFalse();

        assertThat(ResponseStatus.REJECTED.isRejected()).isTrue();
        assertThat(ResponseStatus.ERROR.isRejected()).isFalse();
        assertThat(ResponseStatus.ERROR.isError()).isTrue();
        assertThat(ResponseStatus.ACCEPTED.isError()).isFalse();
        assertThat(ResponseStatus.ACCEPTED.isAccepted()).isTrue();
        assertThat(ResponseStatus.DENIED.isAccepted()).isFalse();
        assertThat(ResponseStatus.DENIED.isDenied()).isTrue();
        assertThat(ResponseStatus.ACCEPTED.isDenied()).isFalse();

    }

    @Test
    public void shouldReturnCorrectName() {
        assertThat(ResponseStatus.ACCEPTED.getName()).isEqualTo("ACCEPTED");
        assertThat(ResponseStatus.ERROR.getName()).isEqualTo("ERROR");
        assertThat(ResponseStatus.DENIED.getName()).isEqualTo("DENIED");
        assertThat(ResponseStatus.REJECTED.getName()).isEqualTo("REJECTED");
        assertThat(ResponseStatus.INVALID_BAN.getName()).isEqualTo("INVALID_BAN");
    }

}
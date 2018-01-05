package com.vodafone.charging.accountservice.domain.enums;

import com.vodafone.charging.accountservice.domain.ChargingId;
import org.junit.Test;

import java.util.Random;

import static com.vodafone.charging.accountservice.domain.ChargingId.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ChargingIdTest {

    @Test
    public void shouldGetAllTypesOfChargingId() {
        assertThat(Type.MSISDN.type()).isEqualTo("msisdn");
        assertThat(Type.VODAFONE_ID.type()).isEqualTo("vodafoneid");
        assertThat(Type.PSTN.type()).isEqualTo("pstn");
        assertThat(Type.STB.type()).isEqualTo("stb");
    }

    @Test
    public void shouldCreateChargingIdSuccessfully() {
        String value = String.valueOf(new Random().nextInt());
        ChargingId chargingId = new Builder()
                .type(Type.VODAFONE_ID)
                .value(value)
                .build();
        assertThat(chargingId.getType()).isEqualTo(Type.VODAFONE_ID);
        assertThat(chargingId.getType().type()).isEqualTo("vodafoneid");
        assertThat(chargingId.getValue()).isEqualTo(value);
    }

}

package com.vodafone.charging.integrationtest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.*;
import com.vodafone.charging.accountservice.domain.enums.RoutableType;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.data.builder.ChargingIdDataBuilder;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import com.vodafone.charging.data.message.JsonConverter;
import com.vodafone.charging.mock.WiremockPreparer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.vodafone.charging.data.ApplicationPortsEnum.DEFAULT_ER_IF_PORT;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by al on 15/01/18.
 * TODO: convert to a full HTTP E2E test against wiremocked ERIF by moving to VfAccountServiceHttpTest
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
public class ERIFClientComponentT {

    @Autowired
    private ERIFClient erifClient;

    @Autowired
    private JsonConverter converter;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(DEFAULT_ER_IF_PORT.value());

    @Test
    public void shouldValidateAccountAndReturnOKAgainstMockedERIF() throws Exception {
        //given

        final ERIFResponse erifResponse = ERIFResponse.builder()
            .status("ACCEPTED").ban("BAN_7777").errId("OK").billingCycleDay(8)
                .build();
        //set expectedInfo to be what we're setting in the mock @TODO expand to all fields
        EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo.Builder(erifResponse.getStatus())
                .ban(erifResponse.getBan()).errorId(erifResponse.getErrId()).billingCycleDay(erifResponse.getBillingCycleDay()).build();
        ChargingId chargingId = ChargingIdDataBuilder.aChargingId();

        WiremockPreparer.prepareForValidateJson(chargingId);

        final ContextData contextData = ContextDataDataBuilder.aContextData(chargingId);
        MessageControl messageControl = new MessageControl(contextData.getLocale());
        Routable routable = new Routable(RoutableType.validate.name(), contextData.getChargingId(), contextData.getClientId(), contextData.isKycCheck());

        //when
        EnrichedAccountInfo enrichedAccountInfo = this.erifClient.validate(messageControl, routable);

        //then
        assertThat(expectedInfo).isEqualToComparingFieldByField(enrichedAccountInfo);

    }
}

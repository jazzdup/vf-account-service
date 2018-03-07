package com.vodafone.charging.integrationtest.http;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.domain.ChargingId;
import com.vodafone.charging.accountservice.domain.ContextData;
import com.vodafone.charging.accountservice.domain.EnrichedAccountInfo;
import com.vodafone.charging.accountservice.dto.json.ERIFResponse;
import com.vodafone.charging.data.builder.ContextDataDataBuilder;
import com.vodafone.charging.mock.WiremockPreparer;
import com.vodafone.charging.properties.PropertiesAccessor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static com.vodafone.charging.data.ApplicationPortsEnum.DEFAULT_ER_IF_PORT;
import static com.vodafone.charging.data.builder.ChargingIdDataBuilder.aChargingId;
import static com.vodafone.charging.data.builder.HttpHeadersDataBuilder.aHttpHeaders;
import static com.vodafone.charging.data.builder.IFResponseDataBuilder.aERIFResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpMethod.POST;


//TODO This test needs to be reviewed.  Wiremock may not be the best approach.
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class, webEnvironment = DEFINED_PORT)
public class LogFileTest {

    private static final Logger log = LoggerFactory.getLogger(LogFileTest.class);

    private String url = "http://localhost:8080/accounts";
    private String erifUrl = "http://localhost:8458/broker/router.jsp";

    @MockBean
    PropertiesAccessor propertiesAccessor;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(DEFAULT_ER_IF_PORT.value());

    @Test
    public void shouldCheckLogFileSizes() throws Exception {
        File logDir = new File("./logs/");
        if (logDir.exists()){
            logDir.delete();
        }

        //given
        given(propertiesAccessor.getPropertyAsBoolean(eq("ulf.logger.without.payload.enable"), anyBoolean())).willReturn(true);
        given(propertiesAccessor.getPropertyAsBoolean(eq("ulf.logger.with.payload.enable"), anyBoolean())).willReturn(true);
        given(propertiesAccessor.getPropertyAsBoolean(eq("ulf.logger.with.pretty.printing.enable"), anyBoolean()    )).willReturn(true);
        given(propertiesAccessor.getPropertyForOpco(eq("erif.url"), anyString())).willReturn(erifUrl);
        final ERIFResponse erifResponse = aERIFResponse();
        //set expectedInfo to be what we're setting in the mock
        final EnrichedAccountInfo expectedInfo = new EnrichedAccountInfo(erifResponse);
        ChargingId chargingId = aChargingId();
        final ContextData contextData = ContextDataDataBuilder.aContextData(chargingId);
        HttpHeaders headers = aHttpHeaders(contextData.getClientId(),
                contextData.getLocale(),
                contextData.getChargingId());
        WiremockPreparer.prepareForValidateJson(chargingId);
        //when
        ResponseEntity<EnrichedAccountInfo> responseEntity = testRestTemplate.exchange(url, POST, new HttpEntity<>(contextData, headers), EnrichedAccountInfo.class);
        EnrichedAccountInfo enrichedAccountInfo = responseEntity.getBody();
        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        File ultimate = new File("./logs/ultimate.log");
        File ulf = new File("./logs/ulf-log-without-payload.log");
        File applog = new File("./logs/vf-account-service.log");
        assertThat(ultimate.exists()).as("check %s exists", ultimate.getName()).isTrue();
        assertThat(ulf.exists()).as("check %s exists", ulf.getName()).isTrue();
        assertThat(applog.exists()).as("check %s exists", applog.getName()).isTrue();
        double ultimateSize2 = ultimate.length();
        double ulfSize2 = ulf.length();
        double vfasSize2 = applog.length();
        assertThat(ultimateSize2).as("check %s is written to", ultimate.getName()).isGreaterThan(1);
        assertThat(ulfSize2).as("check %s is written to", ulf.getName()).isGreaterThan(1);
        assertThat(vfasSize2).as("check %s is written to", applog.getName()).isGreaterThan(1);

    }


}
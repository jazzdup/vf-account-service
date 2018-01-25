package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.service.ERIFClient;
import com.vodafone.charging.accountservice.util.PropertiesAccessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * tests against file-based props by default
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@Slf4j
public class CentralConfigServiceIT{
    @Autowired
    private ERIFClient erifClient;
    @Autowired
    private PropertiesAccessor propertiesAccessor;

    @Test
    public void shouldGetERIFClientPropertyFromFile()
    {
        final String expectedUrl = "http://localhost:8458/broker/router.jsp";
        final String url = propertiesAccessor.getProperty("erif.url");
        assertThat(url).isEqualTo(expectedUrl );
    }

    @Test
    public void shouldGetPropertiesAccessorPropertyFromFile()
    {
        assertThat(propertiesAccessor.getProperty("test1")).isEqualTo("value0");
    }
    @Ignore
    @Test
    public void shouldGetPropertiesAccessorPropertyFromDb()
    {
        //e.g. ./postInstallation.sh -u centralconfiguser -p centralconfiguser -i XE -a er.account.service -f /home/al/dev/componentize/vf-account-service/src/test/resources/web/src/main/resources/CentralConfigDefaultData-LIVE.xml
        assertThat(propertiesAccessor.getProperty("test1")).isEqualTo("value1");//different by default
    }
}

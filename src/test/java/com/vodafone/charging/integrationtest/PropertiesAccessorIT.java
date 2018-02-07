package com.vodafone.charging.integrationtest;

import com.vodafone.charging.accountservice.AccountServiceApplication;
import com.vodafone.charging.accountservice.util.PropertiesAccessor;
import com.vodafone.ppe.common.configuration.CentralConfigurationService;
import com.vodafone.ppe.common.configuration.error.MissingConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * tests against file-based props by default
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountServiceApplication.class)
@Slf4j
public class PropertiesAccessorIT {

    public final int TOTAL_NUMBER_OF_PROPS = 8;
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
        assertThat(propertiesAccessor.getProperty("test2")).isEqualTo("2");
        assertThat(propertiesAccessor.getPropertyAsInt("test2")).isEqualTo(2);
        assertThat(propertiesAccessor.getPropertyAsBoolean("testBooleanFalse")).isEqualTo(false);
        assertThat(propertiesAccessor.getPropertyAsBoolean("testBooleanTrue")).isEqualTo(true);
    }
    @Test
    public void shouldGetDefaultPropsFromFile(){
        final String expected = "default";
        final String prop = propertiesAccessor.getProperty("notthere", expected);
        assertThat(prop).isEqualTo(expected);
        final int intProp = propertiesAccessor.getPropertyAsInt("notthere", 5);
        assertThat(intProp).isEqualTo(5);
        final boolean bProp = propertiesAccessor.getPropertyAsBoolean("notthere", true);
        assertThat(bProp).isEqualTo(true);
    }
    @Test
    public void shouldValidateNumberOfPropsFromFile(){
        assertThat(propertiesAccessor.getPropertiesList().size()).isEqualTo(TOTAL_NUMBER_OF_PROPS);
    }

    @Test
    public void shouldThrowMissingConfigurationException() {
        assertThatThrownBy(() -> propertiesAccessor.getProperty("notthere"))
                .isInstanceOf(MissingConfigurationException.class)
                .hasMessageContaining("Key could not be found in configuration");
    }
    @Test
    public void shouldReturnBasePropertiesProviderOK(){
        assertThat(propertiesAccessor.getProvider().getClass()).isEqualTo(CentralConfigurationService.class);
    }

    @Ignore
    @Test
    public void shouldGetPropertiesAccessorPropertyFromDb()
    {
        //e.g. ./postInstallation.sh -u centralconfiguser -p centralconfiguser -i XE -a er.account.service -f /home/al/dev/componentize/vf-account-service/src/test/resources/centralconfig/CentralConfigDefaultData-LIVE.xml
        assertThat(propertiesAccessor.getProperty("test1")).isEqualTo("value1");//different by default
    }
}

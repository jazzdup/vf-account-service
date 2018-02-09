package com.vodafone.charging.accountservice.properties;

import com.vodafone.ppe.common.configuration.BasePropertiesProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * tests against file-based props by default
 */
@RunWith(MockitoJUnitRunner.class)
public class PropertiesTest {
    @Mock
    private BasePropertiesProvider basePropertiesProvider;
//    @InjectMocks
    private PropertiesAccessor propertiesAccessor;
    @Before
    public void init() {
        initMocks(this);
        propertiesAccessor = new SimplePropertiesAccessor(basePropertiesProvider);
    }
    @Test
    public void shouldGetPropertyOK() {
        final String expectedUrl = "http://localhost:8458/broker/router.jsp";
        when(basePropertiesProvider.getProperty("erif.url")).thenReturn(expectedUrl);
        when(basePropertiesProvider.getProperty(eq("erif.url"), anyString())).thenReturn(expectedUrl);
        when(basePropertiesProvider.getProperty("erif.url.not.there", "DEFAULT")).thenReturn("DEFAULT");

        final String url = propertiesAccessor.getProperty("erif.url");
        assertThat(url).isEqualTo(expectedUrl);

        final String url2 = propertiesAccessor.getProperty("erif.url", "DEFAULT");
        assertThat(url2).isEqualTo(expectedUrl);

        final String url3 = propertiesAccessor.getProperty("erif.url.not.there", "DEFAULT");
        assertThat(url3).isEqualTo("DEFAULT");
    }
    @Test
    public void shouldGetPropertyAsBooleanOK()
    {
        final boolean expectedBoolean = true;
        final String expectedStr = "true";
        when(basePropertiesProvider.getProperty("erif.url")).thenReturn(expectedStr);
        when(basePropertiesProvider.getProperty(eq("erif.url"), anyString())).thenReturn(expectedStr);
        when(basePropertiesProvider.getProperty("erif.url.not.there", "DEFAULT")).thenReturn("DEFAULT");

        final boolean val = propertiesAccessor.getPropertyAsBoolean("erif.url");
        assertThat(val).isEqualTo(expectedBoolean);

        final boolean val2 = propertiesAccessor.getPropertyAsBoolean("erif.url", false);
        assertThat(val2).isEqualTo(expectedBoolean);

        final boolean val3 = propertiesAccessor.getPropertyAsBoolean("erif.url.not.there", false);
        assertThat(val3).isEqualTo(false);
    }
    @Test
    public void shouldGetPropertyAsIntOK()
    {
        final int expectedInt = 5;
        final String expectedStr = "5";
        when(basePropertiesProvider.getProperty("erif.url")).thenReturn(expectedStr);
        when(basePropertiesProvider.getProperty(eq("erif.url"), anyString())).thenReturn(expectedStr);
        when(basePropertiesProvider.getProperty("erif.url.not.there", "DEFAULT")).thenReturn(null);

        final int url = propertiesAccessor.getPropertyAsInt("erif.url");
        assertThat(url).isEqualTo(expectedInt);

        final int url2 = propertiesAccessor.getPropertyAsInt("erif.url", 6);
        assertThat(url2).isEqualTo(expectedInt);

        final int url3 = propertiesAccessor.getPropertyAsInt("erif.url.not.there",7);
        assertThat(url3).isEqualTo(7);
    }

    @Test
    public void shouldGetPropertiesMapOK() {
        final Map expectedMap = new HashMap<String, String>();
        expectedMap.put("key", "value");
        expectedMap.put("key2", "value2");

        when(basePropertiesProvider.configurationData()).thenReturn(expectedMap);

        final Map map = propertiesAccessor.getPropertiesMap();
        String v1 = (String) map.get("key");
        String v2 = (String) map.get("key2");
        assertThat(map.size()).isEqualTo(2);
        assertThat(v1).isEqualTo("value");
        assertThat(v2).isEqualTo("value2");
    }

    /**
     * requires integration test for thorough testing
     */
    @Test
    public void shouldGetPropertiesForOpcoOK(){
        final String expectedUrl = "http://localhost:8458/broker/router.jsp";
        final String expectedUrlGB = expectedUrl + ".GB";
        final String expectedUrlDE = expectedUrl + ".DE";
        when(basePropertiesProvider.getProperty("erif.url", "GB", null, null, null, null)).thenReturn(expectedUrlGB);
        when(basePropertiesProvider.getProperty("erif.url", "DE", null, null, null, null)).thenReturn(expectedUrlDE);
        when(basePropertiesProvider.getProperty("erif.url", null, null, null, null, null)).thenReturn(expectedUrlGB);

        final String url = propertiesAccessor.getPropertyForOpco("erif.url", "GB");
        assertThat(url).isEqualTo(expectedUrlGB);

        final String url2 = propertiesAccessor.getPropertyForOpco("erif.url", "DE");
        assertThat(url2).isEqualTo(expectedUrlDE);

        final String url4 = propertiesAccessor.getPropertyForOpco("erif.url.not.there", "GB", "DEFAULT");
        assertThat(url4).isEqualTo("DEFAULT");
    }
}

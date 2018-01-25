package com.vodafone.charging.centralconfigservice;

import com.vodafone.ppe.common.configuration.CentralConfigurationLoader;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

public class CentralConfigurationLoaderUTest {

	@Test
	public void testLoadsAllThePropertiesFromTheDatabase() {
		final CentralConfigurationLoader loader = prepare("VFG2", "VFG", "LIVE", false, false);
		final Map<String, String> centralConfigMap = loader.loadAllProperties();

		assertThat(centralConfigMap, notNullValue());
		assertThat("Map should not be empty", centralConfigMap.isEmpty(), equalTo(false));
		assertThat(centralConfigMap, hasEntry("vfg.opensso.base.url", "http://dgig-dit-dit1.sp.vodafone.com:30645/sso/opensso/identity"));
	}

	@Test
	public void testReturnsEmptyMapForEmptyDatabase() {
		final CentralConfigurationLoader loader = prepare("VFG2", "VFG", "LIVE", true, false);
		final Map<String, String> centralConfigMap = loader.loadAllProperties();

		assertThat(centralConfigMap, notNullValue());
		assertThat(centralConfigMap.isEmpty(), equalTo(true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void throws_backend_exception_if_there_was_an_error() {
		final CentralConfigurationLoader loader = prepare("VFG2", "VFG", "LIVE", true, true);
		loader.loadAllProperties();
	}

	@Test
	public void testOverwritesDefaultValues() {
		final CentralConfigurationLoader loader = prepare("VFG2", "VFG", "LIVE", false, false);
		final Map<String, String> centralConfigMap = loader.loadAllProperties();

		assertThat(centralConfigMap, notNullValue());
		assertThat("Map should not be empty", centralConfigMap.isEmpty(), equalTo(false));
		assertThat(centralConfigMap, hasEntry("vfg.ula.deployment.path", "/ula"));
	}

	@Test
	public void testReturnsValueFromDefault() {
		final CentralConfigurationLoader loader = prepare("VFG2", "VFG", "LIVE", false, false);
		final Map<String, String> centralConfigMap = loader.loadAllProperties();

		assertThat(centralConfigMap, notNullValue());
		assertThat("Map should not be empty", centralConfigMap.isEmpty(), equalTo(false));
		assertThat(centralConfigMap, hasEntry("vfg.ula.only.available.in.default", "default"));
	}

	private CentralConfigurationLoader prepare(String appname, String opco, String env, boolean empty, boolean emptyDatasource) {
		final CentralConfigurationLoader loader;
		if (!emptyDatasource) {
			final EmbeddedDatabaseBuilder datasource = new EmbeddedDatabaseBuilder().setType(H2).addScript("ddl/001-create_tables-embedded.sql");

			if (!empty) {
				datasource.addScript("ddl/002-data-embedded.sql");
			}

			loader = new CentralConfigurationLoader(datasource.build());

		} else {
			loader = new CentralConfigurationLoader(null);
		}

		loader.setAppname(appname);
		loader.setOpco(opco);
		loader.setEnv(env);

		return loader;
	}
}

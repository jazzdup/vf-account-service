package com.vodafone.charging.accountservice.properties;

import com.vodafone.application.errors.ConfigPropertyMissingException;
import com.vodafone.application.logging.ULFKeys;
import com.vodafone.application.util.ULFThreadLocal;
import com.vodafone.ppe.common.configuration.BasePropertiesProvider;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Map;

@Slf4j
public class SimplePropertiesAccessor implements PropertiesAccessor {

	private final BasePropertiesProvider basePropertiesProvider;

	@Override
	public Map<String, String> getPropertiesMap() {
		return basePropertiesProvider.configurationData();
	}

	public SimplePropertiesAccessor(BasePropertiesProvider basePropertiesProvider) {
		this.basePropertiesProvider = basePropertiesProvider;
	}

	@Override
	public boolean getPropertyAsBoolean(String key) {
		String value = getOptionalProperty(key);
		return Boolean.parseBoolean(value);
	}
	@Override
	public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
		String value = getOptionalProperty(key);
		return value != null ? Boolean.parseBoolean(value) : defaultValue;
	}

	private String getOptionalProperty(String key) {
		return basePropertiesProvider.getProperty(key, null);
	}

	/**
	 * @throws com.vodafone.ppe.common.configuration.error.MissingConfigurationException if the property could not be found
	 */
	@Override
	public String getProperty(String key) {
		return basePropertiesProvider.getProperty(key);
	}

	@Override
	public int getPropertyAsInt(@Nonnull String key) {
		return Integer.parseInt(basePropertiesProvider.getProperty(key));
	}

	@Override
	public int getPropertyAsInt(@Nonnull String key, int defaultValue) {
		final String value = basePropertiesProvider.getProperty(key, null);
		return value != null ? Integer.parseInt(value) : defaultValue;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return basePropertiesProvider.getProperty(key, defaultValue);
	}

	@Override
	public String getPropertyForOpco(String key, String country, String defaultGlobalValue){
		final String value = basePropertiesProvider.getProperty(key, country, null, null, null, null);
		return value != null ? value : defaultGlobalValue;
	}
	@Override
	public String getPropertyForOpco(String key, String country) {
		final String value = basePropertiesProvider.getProperty(key, country, null, null, null, null);
		if (value == null) {
			final String error = "Unable to find a property with key=" + key + " for opco=" + country;
			log.error(error);
			ULFThreadLocal.setValue(ULFKeys.ERROR_CODE, ConfigPropertyMissingException.class.getSimpleName());
			ULFThreadLocal.setValue(ULFKeys.ERROR, error);
			throw new ConfigPropertyMissingException(error);
		}
		return value;
	}


	@Override
	public BasePropertiesProvider getProvider() {
		return basePropertiesProvider;
	}


}

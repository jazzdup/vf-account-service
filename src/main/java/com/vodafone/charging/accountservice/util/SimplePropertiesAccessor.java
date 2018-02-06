package com.vodafone.charging.accountservice.util;

import com.vodafone.ppe.common.configuration.BasePropertiesProvider;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Map;

@Slf4j
public class SimplePropertiesAccessor implements PropertiesAccessor {

	private final BasePropertiesProvider basePropertiesProvider;

	@Override
	public Map<String, String> getPropertiesList() {
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

	@Override
	public boolean isOptionalProperty(String key) {
		return getPropertyAsBoolean(key, false);
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
	public BasePropertiesProvider getProvider() {
		return basePropertiesProvider;
	}

//	@Nonnull
//	private List<String> splitProperty(String property) {
//		final List<String> list = new ArrayList<>();
//		if (StringUtils.isNotEmpty(property)) {
//			final String[] partnerArray = StringUtils.split(property, ",");
//			list.addAll(Arrays.asList(StringUtils.stripAll(partnerArray)));
//		}
//
//		return list;
//	}

}

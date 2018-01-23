package com.vodafone.charging.accountservice.util;

import com.vodafone.application.errors.ConfigPropertyMissingException;
import com.vodafone.ppe.common.configuration.BasePropertiesProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimplePropertiesAccessor implements PropertiesAccessor {

	private static final Logger logger = LoggerFactory.getLogger(SimplePropertiesAccessor.class);

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
	public String getOptionalProperty(String key) {
		return basePropertiesProvider.getProperty(key, null);
	}

	/**
	 * @throws ConfigPropertyMissingException when the property could not be found
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

	@Nonnull
	public List<String> splitProperty(String property) {
		final List<String> list = new ArrayList<>();

		if (StringUtils.isNotEmpty(property)) {
			final String[] partnerArray = StringUtils.split(property, ",");
			for (String s : StringUtils.stripAll(partnerArray)) {
				list.add(s);
			}
		}

		return list;
	}

}

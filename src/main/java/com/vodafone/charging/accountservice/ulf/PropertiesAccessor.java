package com.vodafone.charging.accountservice.ulf;

import com.vodafone.ppe.common.configuration.BasePropertiesProvider;

import java.util.Map;
//import com.vodafone.ppe.common.configuration.Base

public interface PropertiesAccessor {
	String getProperty(String key);
	String getProperty(String key, String defaultValue);

	boolean getPropertyAsBoolean(String key);
	boolean getPropertyAsBoolean(String key, boolean defaultValue);

	int getPropertyAsInt(String key);
	int getPropertyAsInt(String key, int defaultValue);

	Map<String, String> getPropertiesMap();

	/**
	 * Country is 2-digit (capitalised) ISO code at end of property key
	 * separated by a full stop (.)
	 * If property value not found for specific country, will return defaultGlobalValue.
	 */
	String getPropertyForOpco(String key, String country, String defaultGlobalValue);
	/**
	 * Country is 2-digit (capitalised) ISO code at end of property key
	 * separated by a full stop (.)
	 * If property value not found for specific country, will return
	 * property value of key without ISO code if available.
	 */
	String getPropertyForOpco(String key, String country);

	BasePropertiesProvider getProvider();
}

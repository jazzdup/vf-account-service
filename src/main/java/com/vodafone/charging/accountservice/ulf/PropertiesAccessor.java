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

	BasePropertiesProvider getProvider();
}

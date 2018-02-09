package com.vodafone.charging.accountservice.properties;

import com.vodafone.ppe.common.configuration.BasePropertiesProvider;

import java.util.Map;
//import com.vodafone.ppe.common.configuration.Base

public interface PropertiesAccessor {

	String SEPARATOR = ".";

	boolean getPropertyAsBoolean(String key);
	boolean getPropertyAsBoolean(String key, boolean defaultValue);
	boolean isOptionalProperty(String key);

	Map<String, String> getPropertiesList();

	String getProperty(String key);
	String getProperty(String key, String defaultValue);

	int getPropertyAsInt(String key);
	int getPropertyAsInt(String key, int defaultValue);

	BasePropertiesProvider getProvider();
}

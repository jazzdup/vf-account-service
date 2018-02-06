package com.vodafone.charging.accountservice.ulf;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
public class LoggingUtil {
	protected static String getOrCreate(HttpServletRequest servletRequest, String parameter, String header, String cookie) {
		String result = servletRequest.getParameter(parameter);
		if (StringUtils.isNotEmpty(result)) {
			return result;
		}

		result = servletRequest.getHeader(header);
		if (StringUtils.isNotEmpty(result)) {
			return result;
		}

		if (StringUtils.isNotEmpty(cookie) && ArrayUtils.isNotEmpty(servletRequest.getCookies())) {
			for (Cookie c : servletRequest.getCookies()) {
				if (cookie.equals(c.getName())) {
					result = c.getValue();
					break;
				}
			}

			if (StringUtils.isNotEmpty(result)) {
				return result;
			}
		}

		return UUID.randomUUID().toString();
	}


}

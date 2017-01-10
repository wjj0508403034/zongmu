package com.zongmu.service.locale;

public interface LocaleService {
	String getMessage(String key);
	
	String getSubject(String key);

	String getErrorMessage(String errorCode);
}

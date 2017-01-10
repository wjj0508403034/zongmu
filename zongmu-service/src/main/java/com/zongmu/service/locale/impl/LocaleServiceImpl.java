package com.zongmu.service.locale.impl;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import com.zongmu.service.locale.LocaleService;

@Service
public class LocaleServiceImpl implements LocaleService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocaleServiceImpl.class);

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private LocaleResolver LocaleResolver;

	@Autowired(required = true)
	private HttpServletRequest request;

	@Override
	public String getMessage(String key) {
		try {
			return this.messageSource.getMessage(key, null, this.LocaleResolver.resolveLocale(request));
		} catch (Exception ex) {
			LOGGER.warn("Not found {} string in localization file", key);
		}
		return null;
	}

	@Override
	public String getErrorMessage(String errorCode) {
		return this.getMessage("ErrorCode_" + errorCode);
	}

	@Override
	public String getSubject(String key) {
		return this.getMessage("Mail_Subject_" + key);
	}
}

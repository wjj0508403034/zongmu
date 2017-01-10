package com.zongmu.service.configuration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zongmu.service.dto.user.UserInfo;
import com.zongmu.service.entity.User;

@Component
public class BusinessObjectFacade {

	@Autowired
	private ApplicationContext applicationContext;

	public User currentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() != null) {
			UserInfo userInfo = (UserInfo) authentication.getPrincipal();
			if (userInfo != null) {
				return userInfo.getUser();
			}
		}

		return null;
	}
	
	public HttpServletRequest currentRequest(){
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return attrs.getRequest();
	}

	public <T> T getService(Class<T> requiredType) throws BeansException {
		return this.applicationContext.getBean(requiredType);
	}
}

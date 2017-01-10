package com.zongmu.service.email;

import com.zongmu.service.entity.User;
import com.zongmu.service.exception.BusinessException;

public interface EmailService {
	
	void sendRegisterMail(User user) throws BusinessException;
	
	void sendForgetPasswordMail(User user) throws BusinessException;
}

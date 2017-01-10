package com.zongmu.service.internal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zongmu.service.dto.pay.PayParam;
import com.zongmu.service.dto.pay.RequestPayParam;
import com.zongmu.service.entity.RequestPay;
import com.zongmu.service.exception.BusinessException;

public interface RequestPayService {

	void create(RequestPayParam requestPayParam) throws BusinessException;
	
	void pay(Long id, PayParam payParam) throws BusinessException;

	Page<RequestPay> list(Pageable pageable,int payStatus);

	RequestPay getPay(Long id);
}

package com.zongmu.service.internal.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.configuration.BusinessObjectFacade;
import com.zongmu.service.dto.pay.PayParam;
import com.zongmu.service.dto.pay.RequestPayParam;
import com.zongmu.service.entity.RequestPay;
import com.zongmu.service.entity.User;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.PointService;
import com.zongmu.service.internal.service.RequestPayService;
import com.zongmu.service.internal.service.UserService;
import com.zongmu.service.point.PayStatus;
import com.zongmu.service.repository.RequestPayRepository;

@Service
public class RequestPayServiceImpl implements RequestPayService {

	@Autowired
	private BusinessObjectFacade boFacade;

	@Transactional
	@Override
	public void create(RequestPayParam requestPayParam) throws BusinessException {
		User user = this.boFacade.currentUser();
		Long availablePoint = this.boFacade.getService(PointService.class).getAvailablePoint();
		if (requestPayParam.getPoint() > availablePoint) {
			throw new BusinessException(ErrorCode.POINT_REQUEST_PAY_NOT_ENOUGH);
		}
		RequestPay requestPay = new RequestPay();
		requestPay.setPoint(requestPayParam.getPoint());
		requestPay.setCreateTime(DateTime.now());
		requestPay.setUserId(user.getId());
		requestPay.setAccount(user.getAlipayAccount());
		this.boFacade.getService(RequestPayRepository.class).save(requestPay);
		this.boFacade.getService(PointService.class).requestPay(requestPay);
	}

	@Override
	public void pay(Long id, PayParam payParam) throws BusinessException {
		if(this.boFacade.getService(RequestPayRepository.class).isDup(payParam.getTranscationNo())){
			throw new BusinessException(ErrorCode.Pay_TranNO_dup);
		}
		
		RequestPay requestPay = this.boFacade.getService(RequestPayRepository.class).findOne(id);
		if (requestPay != null) {
			requestPay.setTranscationNo(payParam.getTranscationNo());
			requestPay.setMemo(payParam.getMemo());
			requestPay.setStatus(PayStatus.PAYED);
			requestPay.setPayTime(DateTime.now());
			this.boFacade.getService(RequestPayRepository.class).save(requestPay);
		}

	}

	@Override
	public Page<RequestPay> list(Pageable pageable, int payStatus) {
		Page<RequestPay> page = null;
		if (payStatus == 1) {
			page = this.boFacade.getService(RequestPayRepository.class).getList(PayStatus.PENDING, pageable);
		} else if (payStatus == 2) {
			page = this.boFacade.getService(RequestPayRepository.class).getList(PayStatus.PAYED, pageable);
		} else {
			page = this.boFacade.getService(RequestPayRepository.class).findAll(pageable);
		}
		for (RequestPay requestPay : page.getContent()) {
			requestPay.setUserName(this.getUserName(requestPay.getUserId()));
		}
		return page;
	}

	@Override
	public RequestPay getPay(Long id) {
		RequestPay requestPay = this.boFacade.getService(RequestPayRepository.class).findOne(id);
		if (requestPay != null) {
			requestPay.setUserName(this.getUserName(requestPay.getUserId()));
		}

		return requestPay;
	}

	private String getUserName(Long id) {
		User user = this.boFacade.getService(UserService.class).getUser(id);
		if (StringUtils.isEmpty(user.getUserName())) {
			return user.getEmail();
		} else {
			return user.getUserName();
		}
	}

}

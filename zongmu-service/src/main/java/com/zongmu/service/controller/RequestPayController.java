package com.zongmu.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.dto.pay.PayParam;
import com.zongmu.service.dto.pay.RequestPayParam;
import com.zongmu.service.entity.RequestPay;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.RequestPayService;

@Controller
@RequestMapping("/pays")
public class RequestPayController {

	@Autowired
	private RequestPayService requestPayService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<RequestPay> getList(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestParam(value = "payStatus", required = false, defaultValue = "0") int payStatus)
					throws BusinessException {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.requestPayService.list(pageable, payStatus);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void create(@RequestBody RequestPayParam requestPayParam) throws BusinessException {
		this.requestPayService.create(requestPayParam);
	}

	@RequestMapping(value = "/{id}/pay", method = RequestMethod.POST)
	@ResponseBody
	public void pay(@PathVariable("id") Long id, @RequestBody PayParam payParam) throws BusinessException {
		this.requestPayService.pay(id, payParam);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RequestPay getPay(@PathVariable("id") Long id) {
		return this.requestPayService.getPay(id);
	}
}

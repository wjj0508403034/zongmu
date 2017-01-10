package com.zongmu.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.entity.RejectReason;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.RejectReasonService;

@Controller
@RequestMapping("/rejectReasons")
public class RejectReasonController {

	@Autowired
	private RejectReasonService rejectReasonService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<RejectReason> list() {
		return this.rejectReasonService.getRejectReasons();
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void create(@RequestBody RejectReason rejectReason) throws BusinessException {
		this.rejectReasonService.create(rejectReason);
	}

	@RequestMapping(value = "/{rejectReasonId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(@PathVariable("rejectReasonId") Long rejectReasonId) throws BusinessException {
		this.rejectReasonService.delete(rejectReasonId);
	}
	
	@RequestMapping(value = "/{rejectReasonId}/default", method = RequestMethod.POST)
	@ResponseBody
	public void setDefault(@PathVariable("rejectReasonId") Long rejectReasonId) throws BusinessException {
		this.rejectReasonService.setDefault(rejectReasonId);
	}

	@RequestMapping(value = "/{rejectReasonId}", method = RequestMethod.GET)
	@ResponseBody
	public RejectReason get(@PathVariable("rejectReasonId") Long rejectReasonId) throws BusinessException {
		return this.rejectReasonService.get(rejectReasonId);
	}
}

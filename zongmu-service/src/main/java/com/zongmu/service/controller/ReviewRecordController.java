package com.zongmu.service.controller;

import java.util.List;

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

import com.zongmu.service.dto.reviewrecord.BatchRejectObject;
import com.zongmu.service.dto.search.ReviewRecordSearchParam;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.ReviewRecordService;

@Controller
@RequestMapping("/reviewRecords")
public class ReviewRecordController {

	@Autowired
	private ReviewRecordService reviewRecordService;

	@RequestMapping(value = "/my", method = RequestMethod.GET)
	@ResponseBody
	public Page<ReviewRecord> getMyTaskRecords(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestParam(value = "status", required = false, defaultValue = "0") int status) throws BusinessException {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.reviewRecordService.getReviewRecords(pageable, status);
	}
	
	@RequestMapping(value = "/queryReviewRecords", method = RequestMethod.POST)
	@ResponseBody
	public Page<ReviewRecord> queryReviewRecords(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestBody ReviewRecordSearchParam reviewRecordSearchParam) throws BusinessException {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.reviewRecordService.queryReviewRecords(pageable, reviewRecordSearchParam);
	}

	@RequestMapping(value = "/{reviewRecordNo}", method = RequestMethod.GET)
	@ResponseBody
	public ReviewRecord getReviewRecord(@PathVariable("reviewRecordNo") String reviewRecordNo)
			throws BusinessException {
		return this.reviewRecordService.getReviewRecord(reviewRecordNo);
	}

	@RequestMapping(value = "/{reviewRecordNo}/new", method = RequestMethod.POST)
	@ResponseBody
	public void newTask(@PathVariable("reviewRecordNo") String reviewRecordNo) throws BusinessException {
		this.reviewRecordService.newTask(reviewRecordNo);
	}

	@RequestMapping(value = "/{reviewRecordNo}/startReview", method = RequestMethod.POST)
	@ResponseBody
	public void startReview(@PathVariable("reviewRecordNo") String reviewRecordNo) throws BusinessException {
		this.reviewRecordService.startReview(reviewRecordNo);
	}

	@RequestMapping(value = "/batchReviewPass", method = RequestMethod.POST)
	@ResponseBody
	public void acceptTasks(@RequestBody List<String> reviewRecordNos) throws BusinessException {
		this.reviewRecordService.batchReviewPass(reviewRecordNos);
	}

	@RequestMapping(value = "/batchReviewFailed", method = RequestMethod.POST)
	@ResponseBody
	public void rejectTasks(@RequestBody BatchRejectObject batchRejectObject) throws BusinessException {
		this.reviewRecordService.batchReviewFailed(batchRejectObject);
	}
}

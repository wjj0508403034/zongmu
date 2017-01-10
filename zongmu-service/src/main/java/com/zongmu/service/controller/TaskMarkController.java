package com.zongmu.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.entity.mark.TaskMarkRecord;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.mark.TaskMarkRecordService;

@Controller
@RequestMapping("/taskMarks")
public class TaskMarkController {

	@Autowired
	private TaskMarkRecordService taskMarkRecordService;

//	@RequestMapping(method = RequestMethod.POST)
//	@ResponseBody
//	public void save(@RequestBody TaskMarkRecord taskMarkRecord) throws BusinessException {
//		this.taskMarkRecordService.saveRecord(taskMarkRecord);
//	}

	@RequestMapping(value = "/{taskMarkRecordId}", method = RequestMethod.GET)
	@ResponseBody
	public List<TaskMarkRecord> getMarkRecord(@PathVariable("taskMarkRecordId") Long taskMarkRecordId)
			throws BusinessException {
		return this.taskMarkRecordService.getRecords(taskMarkRecordId);
	}
}

package com.zongmu.service.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.dto.newexport.EAssetObject;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.ExportService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.internal.service.impl.NewExportServiceImpl;

@Controller
@RequestMapping(value = "/export")
public class ExportController {

	private static Logger logger = Logger.getLogger(ExportController.class);
	
	@Autowired
	private ExportService exportService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private NewExportServiceImpl newExportServiceImpl;

	@RequestMapping(value = "/assetViewTags", method = RequestMethod.POST)
	@ResponseBody
	public void assetViewTags() {
		//exportService.assetViewTags();
	}

	@RequestMapping(value = "/algorithms", method = RequestMethod.POST)
	@ResponseBody
	public void algorithms() {
		//exportService.algorithms();
	}

	@RequestMapping(value = "/assets/{assetNo}", method = RequestMethod.POST)
	@ResponseBody
	public void assets(@PathVariable("assetNo") String assetNo) throws BusinessException {
		exportService.assets(assetNo);
	}

	@RequestMapping(value = "/assets/{assetNo}/{taskNo}", method = RequestMethod.POST)
	@ResponseBody
	public void tasks(@PathVariable("assetNo") String assetNo, @PathVariable("taskNo") String taskNo)
			throws BusinessException {
		exportService.tasks(assetNo, taskNo);
	}
	
	@RequestMapping(value = "/new/assets/{assetNo}/{taskNo}", method = RequestMethod.POST)
	@ResponseBody
	public EAssetObject newTasks(@PathVariable("assetNo") String assetNo, @PathVariable("taskNo") String taskNo)
			throws BusinessException {
		EAssetObject object = newExportServiceImpl.tasks(assetNo, taskNo);
		exportService.assetViewTags(assetNo);
		exportService.algorithms(assetNo);
		exportService.assets(assetNo);
		return object;
	}
	
	@RequestMapping(value = "/new/assets/pic/{assetNo}/{taskNo}", method = RequestMethod.POST)
	@ResponseBody
	public EAssetObject picTasks(@PathVariable("assetNo") String assetNo, @PathVariable("taskNo") String taskNo)
			throws BusinessException {
		EAssetObject object = newExportServiceImpl.tasks(assetNo, taskNo);
		exportService.assetViewTags(assetNo);
		exportService.algorithms(assetNo);
		exportService.assets(assetNo);
		return object;
	}
}

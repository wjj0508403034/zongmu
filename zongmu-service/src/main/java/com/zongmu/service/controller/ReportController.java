package com.zongmu.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.dto.search.ReportSearchParam;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.ReportService;
import com.zongmu.service.report.bsd.Report;

@Controller
@RequestMapping("/report")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@RequestMapping(value = "/bsd", method = RequestMethod.GET)
	@ResponseBody
	public Report get(@RequestParam("algorithmId") Long algorithmId, @RequestParam("from") Integer from,
			@RequestParam(value = "to", required = false) Integer to) throws BusinessException {
		return this.reportService.getReport(algorithmId, from, to);
	}

	@RequestMapping(value = "/newBSD", method = RequestMethod.GET)
	@ResponseBody
	public Report getNewReport(@RequestParam("algorithmId") Long algorithmId,
			@RequestParam(value = "assetViewItemIds", required = false, defaultValue = "") String assetViewItemIds)
					throws BusinessException {
		List<Long> ids = new ArrayList<>();
		for (String id : assetViewItemIds.split(",")) {
			if (!StringUtils.isEmpty(id)) {
				ids.add(Long.parseLong(id));
			}
		}
		return this.reportService.getNewReport(algorithmId, ids);
	}

	@RequestMapping(value = "/search/{algorithmId}", method = RequestMethod.POST)
	@ResponseBody
	public Report search(@PathVariable("algorithmId") Long algorithmId,
			@RequestBody ReportSearchParam reportSearchParam)
					throws BusinessException {
		return this.reportService.search(algorithmId, reportSearchParam);
	}

}

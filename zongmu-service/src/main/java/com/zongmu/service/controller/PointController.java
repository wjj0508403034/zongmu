package com.zongmu.service.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.PointService;
import com.zongmu.service.point.MyPoint;

@Controller
@RequestMapping("/points")
public class PointController {

	private static Logger logger = Logger.getLogger(PointController.class);

	@Autowired
	private PointService pointService;

	@RequestMapping(value = "/my", method = RequestMethod.GET)
	@ResponseBody
	public MyPoint myPoint(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize)
					throws BusinessException {
		logger.info("Get points ...");
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.pointService.getMyPoint(pageable);
	}
	
	@RequestMapping(value = "/my/avaiablepoint", method = RequestMethod.GET)
	@ResponseBody
	public Long getAvailablePoint(){
		return this.pointService.getAvailablePoint();
	}
}

package com.zongmu.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.entity.Train;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.TrainService;

@Controller
@RequestMapping("/trains")
public class TrainController {

	@Autowired
	private TrainService trainService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Train> list() {
		return this.trainService.getTrains();
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void create(@RequestBody Train train) throws BusinessException {
		this.trainService.create(train);
	}

	@RequestMapping(value = "/{trainId}", method = RequestMethod.GET)
	@ResponseBody
	public Train get(@PathVariable("trainId") Long trainId) {
		return this.trainService.get(trainId);
	}

	@RequestMapping(value = "/{trainId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(@PathVariable("trainId") Long trainId) {
		this.trainService.delete(trainId);
	}

	@RequestMapping(value = "/{trainId}", method = RequestMethod.PUT)
	@ResponseBody
	public void update(@PathVariable("trainId") Long trainId, @RequestBody Train train) throws BusinessException {
		this.trainService.update(trainId, train);
	}
}

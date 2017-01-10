package com.zongmu.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.AlgorithmService;

@Controller
@RequestMapping("/algorithms")
public class AlgorithmController {

	@Autowired
	private AlgorithmService algorithmService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Algorithm> list() {
		return this.algorithmService.getAlgorithms();
	}

	@RequestMapping(value = "/allData", method = RequestMethod.GET)
	@ResponseBody
	public List<Algorithm> listAllData() {
		return this.algorithmService.getAlgorithmsFullData();
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Algorithm create(@RequestBody Algorithm algorithm) throws BusinessException {
		return this.algorithmService.createAlgorithm(algorithm);
	}

	@RequestMapping(value = "/{algorithmId}", method = RequestMethod.GET)
	@ResponseBody
	public Algorithm get(@PathVariable("algorithmId") Long algorithmId) throws BusinessException {
		return this.algorithmService.getAlgorithmDetail(algorithmId);
	}

	@RequestMapping(value = "/{algorithmId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(@PathVariable("algorithmId") Long algorithmId) throws BusinessException {
		this.algorithmService.deleteAlgorithm(algorithmId);
	}

	@RequestMapping(value = "/{algorithmId}", method = RequestMethod.PUT)
	@ResponseBody
	public void update(@PathVariable("algorithmId") Long algorithmId, @RequestBody Algorithm algorithm)
			throws BusinessException {
		this.algorithmService.updateAlgorithm(algorithmId, algorithm);
	}

	@RequestMapping(value = "/{algorithmId}/tags", method = RequestMethod.POST)
	@ResponseBody
	public void setTags(@PathVariable("algorithmId") Long algorithmId, @RequestBody Algorithm algorithm)
			throws BusinessException {
		this.algorithmService.setTag(algorithmId, algorithm);
	}
}

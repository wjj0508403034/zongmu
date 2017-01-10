package com.zongmu.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.entity.ColorTag;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.ColorTagService;

@Controller
@RequestMapping("/colorTags")
public class ColorTagController {

	@Autowired
	private ColorTagService colorTagService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<ColorTag> list() {
		return this.colorTagService.getColorTags();
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void create(@RequestBody ColorTag colorTag) throws BusinessException {
		this.colorTagService.create(colorTag);
	}

	@RequestMapping(value = "/{colorTagId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(@PathVariable("colorTagId") Long colorTagId) throws BusinessException {
		this.colorTagService.delete(colorTagId);
	}

	@RequestMapping(value = "/{colorTagId}", method = RequestMethod.PUT)
	@ResponseBody
	public void update(@PathVariable("colorTagId") Long colorTagId, @RequestBody ColorTag colorTag)
			throws BusinessException {
		this.colorTagService.update(colorTagId, colorTag);
	}
}

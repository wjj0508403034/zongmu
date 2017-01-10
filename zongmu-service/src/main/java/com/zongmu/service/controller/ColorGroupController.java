package com.zongmu.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.entity.ColorGroup;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.ColorGroupService;

@Controller
@RequestMapping("/colorGroups")
public class ColorGroupController {

	@Autowired
	private ColorGroupService groupService;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void create(@RequestBody ColorGroup colorGroup) throws BusinessException {
		this.groupService.createGroup(colorGroup);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	@ResponseBody
	public void update(@PathVariable("id") Long id, @RequestBody ColorGroup colorGroup) throws BusinessException {
		this.groupService.updateGroup(id, colorGroup);
	}
}

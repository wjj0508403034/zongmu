package com.zongmu.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.dto.viewtag.BatchItemParam;
import com.zongmu.service.dto.viewtag.BatchParam;
import com.zongmu.service.entity.ViewTag;
import com.zongmu.service.entity.ViewTagItem;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.ViewTagService;

@Controller
@RequestMapping("/viewtags")
public class ViewTagController {

	@Autowired
	private ViewTagService viewTagService;

	@RequestMapping(value = "/batchCreate", method = RequestMethod.POST)
	@ResponseBody
	public void batchCreate(@RequestBody BatchParam batchParam) throws BusinessException {
		this.viewTagService.batchCreate(batchParam);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ViewTag create(@RequestBody ViewTag viewTag) throws BusinessException {
		return this.viewTagService.create(viewTag);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public void update(@PathVariable("id") Long id, @RequestBody ViewTag viewTag) throws BusinessException {
		this.viewTagService.update(id, viewTag);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ViewTag getViewTag(@PathVariable("id") Long id) throws BusinessException {
		return this.viewTagService.getViewTagDetail(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(@PathVariable("id") Long id) throws BusinessException {
		this.viewTagService.delete(id);
	}

	@RequestMapping(value = "/list/{algorithmId}", method = RequestMethod.GET)
	@ResponseBody
	public List<ViewTag> getViewTags(@PathVariable("algorithmId") Long algorithmId) {
		return this.viewTagService.getViewTags(algorithmId);
	}

	@RequestMapping(value = "/listAll", method = RequestMethod.GET)
	@ResponseBody
	public List<ViewTag> getViewTags() throws BusinessException {
		return this.viewTagService.getAllViewTags();
	}

	@RequestMapping(value = "/{id}/batchCreate", method = RequestMethod.POST)
	@ResponseBody
	public ViewTag batchCreateItems(@PathVariable("id") Long id, @RequestBody BatchItemParam param)
			throws BusinessException {
		return this.viewTagService.batchCreateItems(id, param);
	}

	@RequestMapping(value = "/items/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public void updateItem(@PathVariable("id") Long id, @RequestBody ViewTagItem viewTagItem) throws BusinessException {
		this.viewTagService.updateViewTagItem(id, viewTagItem);
	}

	@RequestMapping(value = "/items/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void updateItem(@PathVariable("id") Long id) throws BusinessException {
		this.viewTagService.deleteViewTagItem(id);
	}
}

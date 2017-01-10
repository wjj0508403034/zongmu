package com.zongmu.service.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.dto.tag.TagItemObject;
import com.zongmu.service.dto.tag.TagObject;
import com.zongmu.service.entity.Tag;
import com.zongmu.service.entity.TagItem;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.TagService;
import com.zongmu.service.util.MapperService;

@Controller
@RequestMapping("/tags")
public class TagController {

	@Autowired
	private TagService tagService;

	@Autowired
	private MapperService mapperService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Tag> getAllTags() {
		return this.tagService.getTags();
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void create(@RequestBody Tag tag) throws BusinessException {
		this.tagService.create(tag);
	}

	@RequestMapping(value = "/{tagId}", method = RequestMethod.GET)
	@ResponseBody
	public Tag addTagItem(@PathVariable("tagId") Long tagId) throws BusinessException {
		return this.tagService.getTagDetail(tagId);
	}

	@RequestMapping(value = "/{tagId}", method = RequestMethod.PATCH)
	@ResponseBody
	public void update(@PathVariable("tagId") Long tagId, @RequestBody TagObject tagObject) throws BusinessException {
		Tag tag = this.mapperService.map(tagObject, Tag.class);
		this.tagService.update(tagId, tag);
	}

	@RequestMapping(value = "/{tagId}", method = RequestMethod.POST)
	@ResponseBody
	public void addTagItem(@PathVariable("tagId") Long tagId, @RequestBody TagItemObject tagItemObject)
			throws BusinessException {
		TagItem tagItem = this.mapperService.map(tagItemObject, TagItem.class);
		this.tagService.addItem(tagId, tagItem);
	}

	@RequestMapping(value = "/{tagId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteTag(@PathVariable("tagId") Long tagId) throws BusinessException {
		this.tagService.delete(tagId);
	}

	@RequestMapping(value = "/{tagId}/batchAdd", method = RequestMethod.POST)
	@ResponseBody
	public void addTagItems(@PathVariable("tagId") Long tagId, @RequestBody List<String> tagItems)
			throws BusinessException {
		for (String tagItemValue : tagItems) {
			if (!StringUtils.isEmpty(tagItemValue)) {
				TagItem tagItem = new TagItem();
				tagItem.setValue(tagItemValue);
				this.tagService.addItem(tagId, tagItem);
			}
		}
	}

	@RequestMapping(value = "/{tagId}/setDefaultValues", method = RequestMethod.POST)
	@ResponseBody
	public void setDefaultValue(@PathVariable("tagId") Long tagId, @RequestBody TagItem tagItem)
			throws BusinessException {
		this.tagService.setDefaultValue(tagId, tagItem);
	}
	
	@RequestMapping(value = "/{tagId}/setMultiDefaultValues", method = RequestMethod.POST)
	@ResponseBody
	public void setMultiDefaultValues(@PathVariable("tagId") Long tagId, @RequestBody List<TagItem> tagItems)
			throws BusinessException {
		this.tagService.setMultiDefaultValues(tagId, tagItems);
	}

	@RequestMapping(value = "/deleteTagItem/{tagItemId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteTagItem(@PathVariable("tagItemId") Long tagItemId) throws BusinessException {
		this.tagService.deleteItem(tagItemId);
	}
}

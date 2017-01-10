package com.zongmu.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.dto.assetviewtag.BatchCreateItemsParam;
import com.zongmu.service.entity.AssetViewTag;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.AssetViewTagService;

@Controller
@RequestMapping("/assetViewTags")
public class AssetViewTagController {

	@Autowired
	private AssetViewTagService assetViewTagService;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void createAssetViewTag(@RequestBody AssetViewTag tag) throws BusinessException {
		assetViewTagService.createViewTag(tag);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<AssetViewTag> getAssetViewTag() {
		return assetViewTagService.getAssetViewTag();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public AssetViewTag getViewTag(@PathVariable("id") Long id) throws BusinessException {
		return assetViewTagService.getAssetViewTag(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public void updateViewTag(@PathVariable("id") Long id, @RequestBody AssetViewTag tag) throws BusinessException {
		assetViewTagService.updateAssetViewTag(id,tag);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteViewTag(@PathVariable("id") Long id) throws BusinessException {
		assetViewTagService.deleteViewTag(id);
	}

	@RequestMapping(value = "/batchCreateItems", method = RequestMethod.POST)
	@ResponseBody
	public void batchCreateViewTagItems(@RequestBody BatchCreateItemsParam param) throws BusinessException {
		assetViewTagService.batchCreateViewTagItems(param);
	}

	@RequestMapping(value = "/items/{id}/default", method = RequestMethod.POST)
	@ResponseBody
	public void setDefaultViewTag(@PathVariable("id") Long id) throws BusinessException {
		assetViewTagService.setDefaultViewTag(id);
	}

	@RequestMapping(value = "/items/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteViewTagItem(@PathVariable("id") Long id) throws BusinessException {
		assetViewTagService.deleteViewTagItem(id);
	}
}

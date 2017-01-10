package com.zongmu.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.dto.asset.BatchAssetTag;
import com.zongmu.service.entity.AssetTag;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.AssetTagService;

@Controller
@RequestMapping("/assetTags")
public class AssetTagController {

	@Autowired
	private AssetTagService assetTagService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<AssetTag> getAssetTags() {
		return this.assetTagService.getAssetTags();
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public void createAssetTag(@RequestBody AssetTag assetTag) throws BusinessException {
		this.assetTagService.create(assetTag);
	}
	
	@RequestMapping(value = "/{assetTagId}/default", method = RequestMethod.POST)
	@ResponseBody
	public void setDefault(@PathVariable("assetTagId") Long assetTagId) throws BusinessException {
		this.assetTagService.setDefault(assetTagId);
	}

	@RequestMapping(value = "/batchCreate", method = RequestMethod.POST)
	@ResponseBody
	public void batchCreateAssetTags(@RequestBody BatchAssetTag batchAssetTag) throws BusinessException {
		this.assetTagService.batchCreate(batchAssetTag);
	}

	@RequestMapping(value = "/{assetTagId}", method = RequestMethod.PUT)
	@ResponseBody
	public void updateAssetTag(@PathVariable("assetTagId") Long assetTagId, @RequestBody AssetTag assetTag)
			throws BusinessException {
		this.assetTagService.update(assetTagId, assetTag);
	}

	@RequestMapping(value = "/{assetTagId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteAssetTag(@PathVariable("assetTagId") Long assetTagId) throws BusinessException {
		this.assetTagService.delete(assetTagId);
	}

}

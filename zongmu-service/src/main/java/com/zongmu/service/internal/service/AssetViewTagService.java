package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.dto.assetviewtag.BatchCreateItemsParam;
import com.zongmu.service.entity.AssetViewTag;
import com.zongmu.service.exception.BusinessException;

public interface AssetViewTagService {

	void createViewTag(AssetViewTag tag) throws BusinessException;

	void batchCreateViewTagItems(BatchCreateItemsParam param) throws BusinessException;

	void setDefaultViewTag(Long id) throws BusinessException;

	void deleteViewTagItem(Long id) throws BusinessException;

	List<AssetViewTag> getAssetViewTag();

	AssetViewTag getAssetViewTag(Long id);

	void deleteViewTag(Long id) throws BusinessException;

	void updateAssetViewTag(Long id, AssetViewTag tag) throws BusinessException;

}

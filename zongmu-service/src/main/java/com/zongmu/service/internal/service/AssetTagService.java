package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.dto.asset.BatchAssetTag;
import com.zongmu.service.entity.AssetTag;
import com.zongmu.service.exception.BusinessException;

public interface AssetTagService {

    void create(AssetTag assetTag) throws BusinessException;

    void update(Long assetTagId, AssetTag assetTag) throws BusinessException;

    void delete(Long id) throws BusinessException;

    List<AssetTag> getAssetTags();
    
    AssetTag getAssetTag(Long id);

	void batchCreate(BatchAssetTag batchAssetTag);

	void setDefault(Long assetTagId) throws BusinessException;
}

package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.dto.asset.BatchAssetTag;
import com.zongmu.service.entity.AssetTag;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.AssetService;
import com.zongmu.service.internal.service.AssetTagService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.repository.AssetTagRepository;

@Service
public class AssetTagServiceImpl implements AssetTagService {

	@Autowired
	private AssetTagRepository assetTagRepo;

	@Autowired
	private AssetService assetService;

	@Autowired
	private TaskService taskService;

	@Override
	public void create(AssetTag assetTag) throws BusinessException {
		boolean exist = this.assetTagRepo.exists(assetTag.getName());
		if (exist) {
			throw new BusinessException(ErrorCode.ASSET_TAG_EXIST);
		}

		assetTag.setId(null);
		this.assetTagRepo.save(assetTag);
	}

	@Transactional
	@Override
	public void batchCreate(BatchAssetTag batchAssetTag) {
		boolean hasDefault = this.assetTagRepo.exists(batchAssetTag.getCategory());
		String[] names = batchAssetTag.getValue().split(";");

		for (String name : names) {
			if(!StringUtils.isEmpty(name)){
				AssetTag assetTag = new AssetTag();
				assetTag.setName(name);
				assetTag.setCategory(batchAssetTag.getCategory());
				if (!hasDefault) {
					assetTag.setDefault(true);
					hasDefault = true;
				}
				this.assetTagRepo.save(assetTag);
			}
		}
	}

	@Override
	public void update(Long assetTagId, AssetTag assetTag) throws BusinessException {
		AssetTag find = this.assetTagRepo.findOne(assetTagId);
		if (find == null) {
			throw new BusinessException(ErrorCode.ASSET_TAG_NOT_FOUND);
		}
		this.assetTagRepo.save(assetTag);
	}

	@Transactional
	@Override
	public void delete(Long id) throws BusinessException {
		Long count = this.assetService.countByAssetTag(id);
		if (count != 0) {
			throw new BusinessException(ErrorCode.ASSET_TAG_IN_USING);
		}

		count = this.taskService.countByAssetTag(id);
		if (count != 0) {
			throw new BusinessException(ErrorCode.ASSET_TAG_IN_USING);
		}

		this.assetTagRepo.delete(id);
	}

	@Override
	public List<AssetTag> getAssetTags() {
		return this.assetTagRepo.getAssetTags();
	}

	@Override
	public AssetTag getAssetTag(Long id) {
		if (id != null) {
			return this.assetTagRepo.findOne(id);
		}
		return null;
	}

	@Transactional
	@Override
	public void setDefault(Long id) throws BusinessException {
		AssetTag tag = this.assetTagRepo.findOne(id);
		if (tag == null) {
			throw new BusinessException(ErrorCode.ASSET_TAG_NOT_FOUND);
		}

		this.assetTagRepo.setDefault(id, tag.getCategory());
		this.assetTagRepo.setOthersUnDefault(id, tag.getCategory());
	}

}

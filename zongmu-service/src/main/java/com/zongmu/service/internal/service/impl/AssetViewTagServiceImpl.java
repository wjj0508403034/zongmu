package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.dto.assetviewtag.BatchCreateItemsParam;
import com.zongmu.service.entity.AssetViewTag;
import com.zongmu.service.entity.AssetViewTagItem;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.AssetViewTagService;
import com.zongmu.service.repository.Asset2AssetViewTagRepository;
import com.zongmu.service.repository.AssetViewTagItemRepository;
import com.zongmu.service.repository.AssetViewTagRepository;

@Service
public class AssetViewTagServiceImpl implements AssetViewTagService {

	@Autowired
	private AssetViewTagRepository assetViewTagRepo;

	@Autowired
	private AssetViewTagItemRepository assetViewTagItemRepo;

	@Override
	public void createViewTag(AssetViewTag tag) throws BusinessException {
		boolean exists = this.assetViewTagRepo.exists(tag.getName());
		if (exists) {
			throw new BusinessException(ErrorCode.ASSET_VIEW_TAG_EXIST);
		}

		this.assetViewTagRepo.save(tag);
	}

	@Override
	public void updateAssetViewTag(Long id, AssetViewTag tag) throws BusinessException {
		AssetViewTag viewTag = this.assetViewTagRepo.findOne(id);
		if (viewTag == null) {
			throw new BusinessException(ErrorCode.ASSET_VIEW_TAG_NOT_FOUND);
		}

		boolean exists = this.assetViewTagRepo.existsOnUpdate(id, tag.getName());
		if (exists) {
			throw new BusinessException(ErrorCode.ASSET_VIEW_TAG_EXIST);
		}

		viewTag.setName(tag.getName());
		this.assetViewTagRepo.save(viewTag);
	}

	@Override
	public List<AssetViewTag> getAssetViewTag() {
		List<AssetViewTag> viewTags = assetViewTagRepo.getAll();
		for (AssetViewTag viewTag : viewTags) {
			viewTag.setItems(assetViewTagItemRepo.getItems(viewTag.getId()));
		}
		return viewTags;
	}

	@Override
	public AssetViewTag getAssetViewTag(Long id) {
		AssetViewTag viewTag = this.assetViewTagRepo.findOne(id);
		if (viewTag != null) {
			viewTag.setItems(assetViewTagItemRepo.getItems(id));
		}

		return viewTag;
	}

	@Autowired
	private Asset2AssetViewTagRepository asset2AssetViewTagRepo;

	@Override
	public void deleteViewTag(Long id) throws BusinessException {
		if (this.asset2AssetViewTagRepo.isAssetViewTagUsed(id)) {
			throw new BusinessException(ErrorCode.TAG_IN_USING);
		}
		AssetViewTag viewTag = this.assetViewTagRepo.findOne(id);
		this.assetViewTagRepo.delete(viewTag);
	}

	@Override
	public void batchCreateViewTagItems(BatchCreateItemsParam param) throws BusinessException {
		AssetViewTag viewTag = this.assetViewTagRepo.findOne(param.getAssetViewTagId());
		if (viewTag == null) {
			throw new BusinessException(ErrorCode.ASSET_VIEW_TAG_NOT_FOUND);
		}

		Long count = this.assetViewTagItemRepo.count(param.getAssetViewTagId());
		for (int index = 0; index < param.getItems().size(); index++) {
			String itemName = param.getItems().get(index);
			AssetViewTagItem item = new AssetViewTagItem();
			item.setAssetViewTagId(param.getAssetViewTagId());
			item.setName(itemName);
			if (count == 0 && index == 0) {
				item.setDefault(true);
				this.assetViewTagItemRepo.save(item);
			} else {
				boolean exists = this.assetViewTagItemRepo.exists(param.getAssetViewTagId(), itemName);
				if (!exists) {
					this.assetViewTagItemRepo.save(item);
				}
			}
		}
	}

	@Transactional
	@Override
	public void setDefaultViewTag(Long id) throws BusinessException {
		AssetViewTagItem item = this.assetViewTagItemRepo.findOne(id);
		if (item == null) {
			throw new BusinessException(ErrorCode.ASSET_VIEW_TAG_NOT_FOUND);
		}

		this.assetViewTagItemRepo.setUnDefault(item.getAssetViewTagId(), id);
		item.setDefault(true);
		this.assetViewTagItemRepo.save(item);
	}

	@Override
	public void deleteViewTagItem(Long id) throws BusinessException {
		if (this.asset2AssetViewTagRepo.isAssetViewTagItemUsed(id)) {
			throw new BusinessException(ErrorCode.TAG_IN_USING);
		}

		AssetViewTagItem item = this.assetViewTagItemRepo.findOne(id);
		if (item != null) {
			this.assetViewTagItemRepo.delete(id);
			if(item.isDefault()){
				List<AssetViewTagItem> items = this.assetViewTagItemRepo.getItems(item.getAssetViewTagId());
				if(items.size() > 0){
					AssetViewTagItem newItem = items.get(0);
					newItem.setDefault(true);
					this.assetViewTagItemRepo.save(newItem);
				}
			}
		}
	}

}

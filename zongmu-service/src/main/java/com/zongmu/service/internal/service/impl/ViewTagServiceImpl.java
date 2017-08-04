package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.dto.viewtag.BatchItemParam;
import com.zongmu.service.dto.viewtag.BatchParam;
import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.ViewTag;
import com.zongmu.service.entity.ViewTagItem;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.AlgorithmService;
import com.zongmu.service.internal.service.ViewTagService;
import com.zongmu.service.repository.TaskItemXViewTagRepository;
import com.zongmu.service.repository.ViewTagItemRepository;
import com.zongmu.service.repository.ViewTagRepository;

@Service
public class ViewTagServiceImpl implements ViewTagService {

	@Autowired
	private ViewTagRepository viewTagRepo;

	@Autowired
	private ViewTagItemRepository viewTagItemRepo;

	@Autowired
	private AlgorithmService algorithmService;

	@Autowired
	private TaskItemXViewTagRepository taskItemXViewTagRepo;

	@Override
	public void batchCreate(BatchParam batchParam) throws BusinessException {
		Algorithm algorithm = this.algorithmService.getAlgorithm(batchParam.getAlgorithmId());

		for (String name : batchParam.getNames()) {
			boolean exists = this.viewTagRepo.exists(algorithm.getId(), name);
			if (!exists) {
				ViewTag tag = new ViewTag();
				tag.setAlgorithmId(algorithm.getId());
				tag.setName(name);
				this.viewTagRepo.save(tag);
			}
		}
	}

	@Transactional
	@Override
	public void deleteViewTagsByAlgorithm(Long algorithmId) {
		List<ViewTag> viewTags = this.viewTagRepo.getViewTags(algorithmId);
		for (ViewTag viewTag : viewTags) {
			this.viewTagItemRepo.deleteViewTagItemsByViewTagId(viewTag.getId());
		}
		this.viewTagRepo.deleteViewTagByAlgorithmId(algorithmId);
	}

	@Override
	public ViewTag create(ViewTag tag) throws BusinessException {
		boolean exists = this.viewTagRepo.exists(tag.getAlgorithmId(), tag.getName());
		if (exists) {
			throw new BusinessException(ErrorCode.VIEW_TAG_NAME_EXISTS);
		}
		return this.viewTagRepo.save(tag);
	}

	@Override
	public void update(Long id, ViewTag tag) throws BusinessException {
		ViewTag old = this.viewTagRepo.findOne(id);
		if (old == null) {
			throw new BusinessException(ErrorCode.VIEW_TAG_NOT_FOUND);
		}

		boolean exists = this.viewTagRepo.checkOnUpdate(id, old.getAlgorithmId(), tag.getName());
		if (exists) {
			throw new BusinessException(ErrorCode.VIEW_TAG_NAME_EXISTS);
		}

		old.setName(tag.getName());
		this.viewTagRepo.save(old);
	}

	@Override
	public ViewTag getViewTagDetail(Long id) throws BusinessException {
		ViewTag tag = this.viewTagRepo.findOne(id);
		if (tag == null) {
			throw new BusinessException(ErrorCode.VIEW_TAG_NOT_FOUND);
		}
		tag.setItems(this.viewTagItemRepo.getItems(tag.getId()));
		return tag;
	}

	@Override
	public ViewTag batchCreateItems(Long id, BatchItemParam batchParam) throws BusinessException {
		ViewTag tag = this.viewTagRepo.findOne(id);
		if (tag == null) {
			throw new BusinessException(ErrorCode.VIEW_TAG_NOT_FOUND);
		}

		for (String name : batchParam.getNames()) {
			boolean exists = this.viewTagItemRepo.checkExistsOnCreate(id, name);
			if (!exists) {
				ViewTagItem tagItem = new ViewTagItem();
				tagItem.setViewTagId(id);
				tagItem.setName(name);
				this.viewTagItemRepo.save(tagItem);
			}
		}
		return null;
	}

	@Override
	public void updateViewTagItem(Long id, ViewTagItem item) throws BusinessException {
		ViewTagItem oldItem = this.viewTagItemRepo.findOne(id);
		if (oldItem == null) {
			throw new BusinessException(ErrorCode.VIEW_TAG_ITEM_NOT_FOUND);
		}

		boolean exists = this.viewTagItemRepo.checkExistsOnUpdate(oldItem.getViewTagId(), id, item.getName());
		if (exists) {
			throw new BusinessException(ErrorCode.VIEW_TAG_ITEM_EXIST);
		}

		oldItem.setName(item.getName());
		this.viewTagItemRepo.save(oldItem);
	}

	@Override
	public void deleteViewTagItem(Long id) throws BusinessException {
		boolean exists = this.taskItemXViewTagRepo.isViewTagItemUsing(id);
		if (exists) {
			throw new BusinessException(ErrorCode.VIEW_TAG_IS_USING);
		}
		this.viewTagItemRepo.delete(id);
	}

	@Override
	public void delete(Long id) throws BusinessException {
		boolean exists = this.taskItemXViewTagRepo.isViewTagUsing(id);
		if (exists) {
			throw new BusinessException(ErrorCode.VIEW_TAG_IS_USING);
		}
		this.viewTagRepo.delete(id);
	}

	@Override
	public List<ViewTag> getViewTags(Long algorithmId) {
		List<ViewTag> tags = this.viewTagRepo.getViewTags(algorithmId);
		for (ViewTag tag : tags) {
			tag.setItems(this.viewTagItemRepo.getItems(tag.getId()));
		}
		return tags;
	}

	@Override
	public List<ViewTag> getAllViewTags() throws BusinessException {
		List<ViewTag> tags = this.viewTagRepo.getAllViewTags();
		for (ViewTag tag : tags) {
			Algorithm algorithm = this.algorithmService.getAlgorithm(tag.getAlgorithmId());
			tag.setAlgorithm(algorithm);
			tag.setItems(this.viewTagItemRepo.getItems(tag.getId()));
		}
		return tags;
	}
	
	@Override
	public List<ViewTag> getSimpleAllViewTags(){
		return this.viewTagRepo.getAllViewTags();
	}

	@Override
	public List<ViewTagItem> getAllViewTagItems() {
		return this.viewTagItemRepo.all();
	}

}

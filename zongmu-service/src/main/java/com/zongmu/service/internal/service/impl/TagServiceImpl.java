package com.zongmu.service.internal.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.Tag;
import com.zongmu.service.entity.TagItem;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.TagService;
import com.zongmu.service.internal.service.mark.TaskMarkRecordRefTagService;
import com.zongmu.service.repository.TagItemRepository;
import com.zongmu.service.repository.TagRepository;
import com.zongmu.service.repository.mark.TaskMarkRecordRefTagRepository;
import com.zongmu.service.util.MapperService;

@Service
public class TagServiceImpl implements TagService {

	@Autowired
	private TagRepository tagRepo;

	@Autowired
	private TagItemRepository tagItemRepo;

	@Autowired
	private MapperService mapperService;

	@Autowired
	private TaskMarkRecordRefTagService taskMarkRecordRefTagService;

	@Override
	public List<Tag> getTags() {
		List<Tag> tags = this.mapperService.toList(this.tagRepo.findAll());
		for (Tag tag : tags) {
			tag.setItems(this.tagItemRepo.getTagItems(tag.getId()));
		}

		return tags;
	}

	@Override
	public Tag getTagDetail(Long tagId) throws BusinessException {
		Tag tag = this.tagRepo.findOne(tagId);
		if (tag == null) {
			throw new BusinessException(ErrorCode.TAG_NOT_EXIST);
		}
		tag.setItems(this.tagItemRepo.getTagItems(tag.getId()));

		return tag;
	}

	@Override
	public void create(Tag tag) throws BusinessException {
		boolean exist = this.tagRepo.exists(tag.getName().trim(), tag.getAlgorithmId());
		if (exist) {
			throw new BusinessException(ErrorCode.TAG_EXIST);
		}

		this.tagRepo.save(tag);
	}

	@Override
	public void update(Long tagId, Tag tag) throws BusinessException {
		Tag oldTag = this.tagRepo.findOne(tagId);
		if (tag == null) {
			throw new BusinessException(ErrorCode.TAG_NOT_EXIST);
		}

		boolean exist = this.tagRepo.exists(tag.getName().trim(), tag.getAlgorithmId());
		if (exist) {
			throw new BusinessException(ErrorCode.TAG_EXIST);
		}

		oldTag.setName(tag.getName());

		this.tagRepo.save(oldTag);
	}

	@Override
	public void addItem(Long tagId, TagItem tagItem) throws BusinessException {
		Tag tag = this.getTagDetail(tagId);
		if (tag.getItems().size() == 0) {
			tagItem.setDefault(true);
		}

		TagItem oldTagItem = this.tagItemRepo.getTagItem(tagId, tagItem.getValue());
		if (oldTagItem == null) {
			tagItem.setTagId(tagId);
			this.tagItemRepo.save(tagItem);
		}
	}

	@Override
	public void updateItem(Long tagId, TagItem tagItem) throws BusinessException {
		checkBeforeUpdate(tagId, tagItem);
		this.tagItemRepo.save(tagItem);
	}

	@Transactional
	@Override
	public void deleteItem(Long tagItemId) throws BusinessException {
		Long count = this.taskMarkRecordRefTagService.countByTagItem(tagItemId);
		if (count != 0) {
			throw new BusinessException(ErrorCode.TAGITEM_IN_USING);
		}
		TagItem tagItem = this.tagItemRepo.findOne(tagItemId);
		this.tagItemRepo.delete(tagItemId);
		TagItem first = this.tagItemRepo.first(tagItem.getTagId());
		if (first != null) {
			first.setDefault(true);
			this.tagItemRepo.save(first);
		}
	}

	private void checkBeforeUpdate(Long tagId, TagItem tagItem) throws BusinessException {
		Tag tag = this.tagRepo.findOne(tagId);
		if (tag == null) {
			throw new BusinessException(ErrorCode.TAG_NOT_EXIST);
		}

		if (this.tagItemRepo.getTagItem(tagItem.getId(), tag.getId()) == null) {
			throw new BusinessException(ErrorCode.TAG_ITEM_NULL);
		}
	}

	@Transactional
	@Override
	public void setDefaultValue(Long tagId, TagItem tagItem) throws BusinessException {

		boolean exist = this.tagItemRepo.exists(tagItem.getId());
		if (!exist) {
			throw new BusinessException(ErrorCode.TAG_NOT_EXIST);
		}

		this.tagItemRepo.setDefault(tagId, tagItem.getId());
		this.tagItemRepo.setOthersUnDefault(tagId, tagItem.getId());
	}

	@Transactional
	@Override
	public void setMultiDefaultValues(Long tagId, List<TagItem> tagItems) {
		this.tagItemRepo.setAllUnDefault(tagId);
		List<Long> ids = new ArrayList<>();
		for (TagItem tagItem : tagItems) {
			ids.add(tagItem.getId());
		}

		this.tagItemRepo.setMultiDefault(tagId, ids);

	}

	@Transactional
	@Override
	public void delete(Long tagId) throws BusinessException {
		if (this.tagIsUsing(tagId)) {
			throw new BusinessException(ErrorCode.TAG_IN_USING);
		}
		this.tagRepo.delete(tagId);
		this.tagItemRepo.deleteTagItems(tagId);
	}

	@Autowired
	private TaskMarkRecordRefTagRepository taskMarkRecordRefTag;

	private boolean tagIsUsing(Long tagId) {
		return this.taskMarkRecordRefTag.checkBeforeDelete(tagId);
	}

	@Override
	public TagItem getTagItem(Long tagItemId) {
		return this.tagItemRepo.findOne(tagItemId);
	}

	@Override
	public List<Tag> getTags(List<Long> ids) {
		return this.mapperService.toList(this.tagRepo.findAll(ids));
	}

	@Override
	public List<Tag> getTagsByAlgorithm(Long algorithmId) {
		List<Tag> tags = this.tagRepo.getTagsByAlgorithm(algorithmId);
		for (Tag tag : tags) {
			tag.setItems(this.tagItemRepo.getTagItems(tag.getId()));
		}
		return tags;
	}

	@Override
	public void deleteTagsByAlgorithm(Long algorithmId) {
		List<Tag> tags = this.tagRepo.getTagsByAlgorithm(algorithmId);
		for (Tag tag : tags) {
			this.tagItemRepo.deleteTagItems(tag.getId());
		}
		this.tagRepo.deleteTagsByAlgorithm(algorithmId);
	}

}

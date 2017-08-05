package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.entity.Tag;
import com.zongmu.service.entity.TagItem;
import com.zongmu.service.exception.BusinessException;

public interface TagService {

	List<Tag> getTags();

	void create(Tag tag) throws BusinessException;

	void update(Long tagId, Tag tag) throws BusinessException;

	Tag getTagDetail(Long tagId) throws BusinessException;

	void addItem(Long tagId, TagItem tagItem) throws BusinessException;

	void updateItem(Long tagId, TagItem tagItem) throws BusinessException;

	void deleteItem(Long tagItemId) throws BusinessException;

	void setDefaultValue(Long tagId, TagItem tagItem) throws BusinessException;

	void delete(Long tagId) throws BusinessException;

	TagItem getTagItem(Long tagItemId);

	List<Tag> getTags(List<Long> ids);
	
	List<Tag> getTagsByAlgorithm(Long algorithmId);
	
	void deleteTagsByAlgorithm(Long algorithmId);

	void setMultiDefaultValues(Long tagId, List<TagItem> tagItems);

	List<Tag> getSimpleTagsByAlgorithm(Long algorithmId);

	List<TagItem> getTagItemsByTagId(Long tagId);
}

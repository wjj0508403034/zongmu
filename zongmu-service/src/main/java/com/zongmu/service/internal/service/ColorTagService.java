package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.entity.ColorTag;
import com.zongmu.service.exception.BusinessException;

public interface ColorTagService {

	List<ColorTag> getColorTags();

	void create(ColorTag colorTag) throws BusinessException;

	void delete(Long id) throws BusinessException;

	void update(Long id, ColorTag colorTag) throws BusinessException;
	
	ColorTag getTag(Long id);
	
	List<ColorTag> getTagsByGroup(Long groupId);

	void deleteByGroupId(Long id);
}

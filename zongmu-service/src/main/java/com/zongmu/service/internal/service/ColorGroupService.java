package com.zongmu.service.internal.service;

import com.zongmu.service.entity.ColorGroup;
import com.zongmu.service.exception.BusinessException;

public interface ColorGroupService {

	ColorGroup getColorGroupDetail(Long algorithmId);

	void createGroup(ColorGroup colorGroup) throws BusinessException;

	void updateGroup(Long id, ColorGroup colorGroup) throws BusinessException;
	
	void deleteGroupByAlgorithm(Long algorithmId);
}

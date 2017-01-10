package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.dto.viewtag.BatchItemParam;
import com.zongmu.service.dto.viewtag.BatchParam;
import com.zongmu.service.entity.ViewTag;
import com.zongmu.service.entity.ViewTagItem;
import com.zongmu.service.exception.BusinessException;

public interface ViewTagService {

	void batchCreate(BatchParam batchParam) throws BusinessException;

	void update(Long id, ViewTag tag) throws BusinessException;

	void delete(Long id) throws BusinessException;

	List<ViewTag> getViewTags(Long algorithmId);

	ViewTag create(ViewTag tag) throws BusinessException;

	ViewTag getViewTagDetail(Long id) throws BusinessException;

	ViewTag batchCreateItems(Long id, BatchItemParam param) throws BusinessException;

	void updateViewTagItem(Long id, ViewTagItem item) throws BusinessException;

	void deleteViewTagItem(Long id) throws BusinessException;

	List<ViewTag> getAllViewTags() throws BusinessException;

	void deleteViewTagsByAlgorithm(Long id);
}

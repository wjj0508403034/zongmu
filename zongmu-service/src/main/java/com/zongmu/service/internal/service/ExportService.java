package com.zongmu.service.internal.service;

import com.zongmu.service.exception.BusinessException;

public interface ExportService {

	void assetViewTags(String assetNo);

	void algorithms(String assetNo);

	void assets(String assetNo) throws BusinessException;

	void tasks(String assetNo, String taskNo) throws BusinessException;
}

package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.Tag;
import com.zongmu.service.exception.BusinessException;

public interface AlgorithmService {

	List<Algorithm> getAlgorithms();

	Algorithm getAlgorithm(Long id) throws BusinessException;

	void updateAlgorithm(Long id, Algorithm algorithm) throws BusinessException;

	void setTag(Long id, Algorithm algorithm) throws BusinessException;

	Algorithm createAlgorithm(Algorithm algorithm) throws BusinessException;

	void deleteAlgorithm(Long id) throws BusinessException;
	
	List<Tag> getTags(Long algorithmId) throws BusinessException;

	Algorithm getAlgorithmDetail(Long id) throws BusinessException;

	List<Algorithm> getAlgorithmsFullData();
}

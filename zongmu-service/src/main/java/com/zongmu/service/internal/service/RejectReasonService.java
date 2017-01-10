package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.entity.RejectReason;
import com.zongmu.service.exception.BusinessException;

public interface RejectReasonService {

	List<RejectReason> getRejectReasons();

	void create(RejectReason rejectReason) throws BusinessException;

	void delete(Long id) throws BusinessException;

	RejectReason get(Long id) throws BusinessException;

	void setDefault(Long id) throws BusinessException;
}

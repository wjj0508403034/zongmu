package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.RejectReason;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.RejectReasonService;
import com.zongmu.service.internal.service.ReviewRecordService;
import com.zongmu.service.repository.RejectReasonRepository;
import com.zongmu.service.util.MapperService;

@Service
public class RejectReasonServiceImpl implements RejectReasonService {

	@Autowired
	private RejectReasonRepository rejectReasonRepo;

	@Autowired
	private MapperService mapperService;

	@Autowired
	private ReviewRecordService reviewRecordService;

	@Override
	public List<RejectReason> getRejectReasons() {
		return this.mapperService.toList(this.rejectReasonRepo.findAll());
	}

	@Transactional
	@Override
	public void create(RejectReason rejectReason) throws BusinessException {
		boolean exist = this.rejectReasonRepo.exist(rejectReason.getDescription());
		if (exist) {
			throw new BusinessException(ErrorCode.REJECT_REASON_EXIST);
		}

		boolean hasDefault = this.rejectReasonRepo.hasDefault();
		if (!hasDefault) {
			rejectReason.setDefault(true);
		}

		this.rejectReasonRepo.save(rejectReason);
	}

	@Transactional
	@Override
	public void delete(Long id) throws BusinessException {
		Long count = this.reviewRecordService.countByReason(id);
		if (count != 0) {
			throw new BusinessException(ErrorCode.REJECT_REASON_IN_USING);
		}

		this.rejectReasonRepo.delete(id);

		RejectReason reason = this.rejectReasonRepo.first();
		if (reason != null) {
			reason.setDefault(true);
			this.rejectReasonRepo.save(reason);
		}
	}

	@Override
	public RejectReason get(Long id) throws BusinessException {
		RejectReason reason = this.rejectReasonRepo.findOne(id);
		if (reason == null) {
			throw new BusinessException(ErrorCode.REJECT_REASON_NOT_EXIST);
		}

		return reason;
	}

	@Transactional
	@Override
	public void setDefault(Long id) throws BusinessException {
		boolean exist = this.rejectReasonRepo.exists(id);
		if (!exist) {
			throw new BusinessException(ErrorCode.REJECT_REASON_NOT_EXIST);
		}

		this.rejectReasonRepo.setDefault(id);
		this.rejectReasonRepo.setOthersUnDefault(id);
	}

}

package com.zongmu.service.dto.reviewrecord;

import java.util.List;

public class BatchRejectObject extends RejectReasonObject {

	private List<String> reviewRecordNos;

	public List<String> getReviewRecordNos() {
		return reviewRecordNos;
	}

	public void setReviewRecordNos(List<String> reviewRecordNos) {
		this.reviewRecordNos = reviewRecordNos;
	}
}

package com.zongmu.service.dto.pay;

import com.zongmu.service.point.PayStatus;

public class PayFilterParam {

	private PayStatus status;

	public PayStatus getStatus() {
		return status;
	}

	public void setStatus(PayStatus status) {
		this.status = status;
	}
}

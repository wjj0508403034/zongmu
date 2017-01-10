package com.zongmu.service.internal.service;

import org.springframework.data.domain.Pageable;

import com.zongmu.service.entity.RequestPay;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.point.MyPoint;

public interface PointService {

	MyPoint getMyPoint(Pageable pageable);

	void create(ReviewRecord reviewRecord, TaskRecord taskRecord);
	
	void requestPay(RequestPay pay);
	
	Long getAvailablePoint();

}

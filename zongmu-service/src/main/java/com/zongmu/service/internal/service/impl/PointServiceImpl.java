package com.zongmu.service.internal.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.zongmu.service.configuration.BusinessObjectFacade;
import com.zongmu.service.entity.RequestPay;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.entity.User;
import com.zongmu.service.entity.UserPoint;
import com.zongmu.service.internal.service.PointService;
import com.zongmu.service.point.MyPoint;
import com.zongmu.service.point.UserPointType;
import com.zongmu.service.repository.UserPointRepository;

@Service
public class PointServiceImpl implements PointService {

	@Autowired
	private UserPointRepository userPointRepo;

	@Autowired
	private BusinessObjectFacade boFacade;

	@Override
	public MyPoint getMyPoint(Pageable pageable) {
		User user = this.boFacade.currentUser();
		MyPoint myPoint = new MyPoint();
		myPoint.setUser(user);
		myPoint.setPoint(this.userPointRepo.sumMyPoint(user.getId()));
		myPoint.setPoints(this.userPointRepo.getPoints(user.getId(), pageable));
		return myPoint;
	}

	@Override
	public void create(ReviewRecord reviewRecord, TaskRecord taskRecord) {
		boolean exists = this.userPointRepo.exists(reviewRecord.getTaskItemNo(), reviewRecord.getUserId() * 1l);
		if (exists) {
			return;
		}

		UserPoint userPoint = new UserPoint();
		userPoint.setUserId(taskRecord.getUserId());
		userPoint.setTaskItemNo(reviewRecord.getTaskItemNo());
		userPoint.setType(UserPointType.INPUT);
		userPoint.setPoint(taskRecord.getPoint());
		userPoint.setCreateTime(DateTime.now());
		this.userPointRepo.save(userPoint);
	}

	@Override
	public void requestPay(RequestPay pay) {
		User user = this.boFacade.currentUser();
		UserPoint userPoint = new UserPoint();
		userPoint.setUserId(user.getId());
		userPoint.setType(UserPointType.OUTPUT);
		userPoint.setPoint(pay.getPoint() * -1);
		userPoint.setCreateTime(DateTime.now());
		userPoint.setPayId(pay.getId());
		this.userPointRepo.save(userPoint);
	}

	@Override
	public Long getAvailablePoint() {
		User user = this.boFacade.currentUser();
		return this.userPointRepo.sumMyPoint(user.getId());
	}

}

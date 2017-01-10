package com.zongmu.service.point;

import org.springframework.data.domain.Page;

import com.zongmu.service.entity.User;
import com.zongmu.service.entity.UserPoint;

public class MyPoint {

	private Long point;
	
	private User user;

	private Page<UserPoint> points;

	public Long getPoint() {
		return point;
	}

	public void setPoint(Long point) {
		this.point = point;
	}

	public Page<UserPoint> getPoints() {
		return points;
	}

	public void setPoints(Page<UserPoint> points) {
		this.points = points;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}

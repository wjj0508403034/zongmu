package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.joda.time.DateTime;

import com.zongmu.service.point.UserPointType;

@Entity
@Table
public class UserPoint implements Serializable {

	private static final long serialVersionUID = -8168086398145587873L;

	@Id
	@SequenceGenerator(name = "USERPOINT_SEQUENCE", sequenceName = "USERPOINT_SEQUENCE")
	@GeneratedValue(generator = "USERPOINT_SEQUENCE", strategy = GenerationType.AUTO)
	private long Id;

	private UserPointType type;

	private int point;

	private String taskItemNo;
	
	private long userId;
	
	private DateTime createTime;
	
	private Long payId;

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public UserPointType getType() {
		return type;
	}

	public void setType(UserPointType type) {
		this.type = type;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getTaskItemNo() {
		return taskItemNo;
	}

	public void setTaskItemNo(String taskItemNo) {
		this.taskItemNo = taskItemNo;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public Long getPayId() {
		return payId;
	}

	public void setPayId(Long payId) {
		this.payId = payId;
	}
}

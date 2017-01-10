package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import com.zongmu.service.point.PayStatus;

@Entity
@Table
public class RequestPay implements Serializable {

	private static final long serialVersionUID = 4037044909117519456L;

	@Id
	@SequenceGenerator(name = "RequestPay_SEQUENCE", sequenceName = "RequestPay_SEQUENCE")
	@GeneratedValue(generator = "RequestPay_SEQUENCE", strategy = GenerationType.AUTO)
	private long id;

	private long userId;

	@Transient
	private String userName;

	private int point;

	private String account;

	private DateTime createTime;

	private DateTime payTime;

	private String transcationNo;
	
	private String memo;

	private PayStatus status = PayStatus.PENDING;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public DateTime getPayTime() {
		return payTime;
	}

	public void setPayTime(DateTime payTime) {
		this.payTime = payTime;
	}

	public String getTranscationNo() {
		return transcationNo;
	}

	public void setTranscationNo(String transcationNo) {
		this.transcationNo = transcationNo;
	}

	public PayStatus getStatus() {
		return status;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setStatus(PayStatus status) {
		this.status = status;
	}

}

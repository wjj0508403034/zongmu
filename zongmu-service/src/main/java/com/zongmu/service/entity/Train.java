package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class Train implements Serializable {

	private static final long serialVersionUID = -8798579505169079450L;

	@Id
	@SequenceGenerator(name = "TRAIN_SEQUENCE", sequenceName = "TRAIN_SEQUENCE")
	@GeneratedValue(generator = "TRAIN_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String subject;

	@Lob
	@Column(length = 100000)
	private String body;

	private Integer orderNo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

}

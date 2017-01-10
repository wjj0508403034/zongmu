package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class RejectReason implements Serializable {

	private static final long serialVersionUID = -7532338371891998054L;

	@Id
	@SequenceGenerator(name = "REJECTREASON_SEQUENCE", sequenceName = "REJECTREASON_SEQUENCE")
	@GeneratedValue(generator = "REJECTREASON_SEQUENCE", strategy = GenerationType.AUTO)
	private long id;

	private String description;

	private boolean isDefault;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
}

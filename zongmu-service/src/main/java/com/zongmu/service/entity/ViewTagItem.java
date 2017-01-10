package com.zongmu.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class ViewTagItem {

	@Id
	@SequenceGenerator(name = "ViewTagItem_SEQUENCE", sequenceName = "ViewTagItem_SEQUENCE")
	@GeneratedValue(generator = "ViewTagItem_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private Long viewTagId;
	
	@Column
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getViewTagId() {
		return viewTagId;
	}

	public void setViewTagId(Long viewTagId) {
		this.viewTagId = viewTagId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}

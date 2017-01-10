package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class TagItem implements Serializable {

	private static final long serialVersionUID = -8244931794016468345L;

	@Id
	@SequenceGenerator(name = "TAGITEM_SUQUENCE", sequenceName = "TAGITEM_SUQUENCE")
	@GeneratedValue(generator = "TAGITEM_SUQUENCE",strategy = GenerationType.AUTO)
	private Long id;

	private Long tagId;

	private String value;

	private boolean isDefault;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}
}

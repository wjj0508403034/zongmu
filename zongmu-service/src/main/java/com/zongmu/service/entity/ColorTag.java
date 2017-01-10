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
public class ColorTag implements Serializable {

	private static final long serialVersionUID = -9147144060783097974L;

	@Id
	@SequenceGenerator(name = "COLOR_TAG_SEQUENCE", sequenceName = "COLOR_TAG_SEQUENCE")
	@GeneratedValue(generator = "COLOR_TAG_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private String color;

	private Long colorGroupId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Long getColorGroupId() {
		return colorGroupId;
	}

	public void setColorGroupId(Long colorGroupId) {
		this.colorGroupId = colorGroupId;
	}
}

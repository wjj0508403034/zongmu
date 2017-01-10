package com.zongmu.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table
public class Algorithm implements Serializable {

	private static final long serialVersionUID = 3265211866841103769L;

	@Id
	@SequenceGenerator(name = "Algorithm_SEQUENCE", sequenceName = "Algorithm_SEQUENCE")
	@GeneratedValue(generator = "Algorithm_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String name;

	@Transient
	private List<Tag> tags = new ArrayList<>();

	@Transient
	private List<ViewTag> viewTags = new ArrayList<>();

	@Transient
	private ColorGroup colorGroup;

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

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public ColorGroup getColorGroup() {
		return colorGroup;
	}

	public void setColorGroup(ColorGroup colorGroup) {
		this.colorGroup = colorGroup;
	}

	public List<ViewTag> getViewTags() {
		return viewTags;
	}

	public void setViewTags(List<ViewTag> viewTags) {
		this.viewTags = viewTags;
	}
}

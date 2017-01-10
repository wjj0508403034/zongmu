package com.zongmu.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table
public class ColorGroup implements Serializable {

	private static final long serialVersionUID = 110697102432663681L;

	@Id
	@SequenceGenerator(name = "COLOR_GROUP_SEQUENCE", sequenceName = "COLOR_GROUP_SEQUENCE")
	@GeneratedValue(generator = "COLOR_GROUP_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private Long algorithmId;

	@Transient
	private List<ColorTag> tags = new ArrayList<>();

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

	public Long getAlgorithmId() {
		return algorithmId;
	}

	public void setAlgorithmId(Long algorithmId) {
		this.algorithmId = algorithmId;
	}

	public List<ColorTag> getTags() {
		return tags;
	}

	public void setTags(List<ColorTag> tags) {
		this.tags = tags;
	}
}

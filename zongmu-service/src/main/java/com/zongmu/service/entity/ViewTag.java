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
public class ViewTag implements Serializable {

	private static final long serialVersionUID = 7999911748122093960L;

	@Id
	@SequenceGenerator(name = "VIEWTAG_SEQUENCE", sequenceName = "VIEWTAG_SEQUENCE")
	@GeneratedValue(generator = "VIEWTAG_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private Long algorithmId;
	
	private boolean readonly;
	
	@Transient
	private Algorithm algorithm;

	@Transient
	private List<ViewTagItem> items = new ArrayList<>();

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

	public List<ViewTagItem> getItems() {
		return items;
	}

	public void setItems(List<ViewTagItem> items) {
		this.items = items;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
}

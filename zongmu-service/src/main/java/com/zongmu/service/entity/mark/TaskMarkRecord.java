package com.zongmu.service.entity.mark;

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

import com.zongmu.service.entity.ColorTag;

@Entity
@Table
public class TaskMarkRecord {

	@Id
	@SequenceGenerator(name = "TASKMARKRECORD_SEQUENCE", sequenceName = "TASKMARKRECORD_SEQUENCE")
	@GeneratedValue(generator = "TASKMARKRECORD_SEQUENCE",strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private Long taskRecordId;

	@Column
	private String name;

	@Column
	private String color;

	@Column
	private Long colorTagId;
	
	@Column
	private Integer startIndex;

	@Column
	private Integer endIndex;

	@Transient
	private List<TaskMarkGroup> groups = new ArrayList<>();

	@Transient
	private List<TaskMarkRecordRefTag> tags = new ArrayList<>();
	
	@Transient
	private List<ShapeFrameIndexInfo> shapeFrameIndexInfos = new ArrayList<>();

	@Transient
	private ColorTag colorTag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskRecordId() {
		return taskRecordId;
	}

	public void setTaskRecordId(Long taskRecordId) {
		this.taskRecordId = taskRecordId;
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

	public Long getColorTagId() {
		return colorTagId;
	}

	public void setColorTagId(Long colorTagId) {
		this.colorTagId = colorTagId;
	}

	public List<TaskMarkRecordRefTag> getTags() {
		return tags;
	}

	public void setTags(List<TaskMarkRecordRefTag> tags) {
		this.tags = tags;
	}

	public ColorTag getColorTag() {
		return colorTag;
	}

	public void setColorTag(ColorTag colorTag) {
		this.colorTag = colorTag;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(Integer endIndex) {
		this.endIndex = endIndex;
	}

	public List<TaskMarkGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<TaskMarkGroup> groups) {
		this.groups = groups;
	}

	public List<ShapeFrameIndexInfo> getShapeFrameIndexInfos() {
		return shapeFrameIndexInfos;
	}

	public void setShapeFrameIndexInfos(List<ShapeFrameIndexInfo> shapeFrameIndexInfos) {
		this.shapeFrameIndexInfos = shapeFrameIndexInfos;
	}
}

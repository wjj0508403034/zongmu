package com.zongmu.service.entity.mark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class ShapeFrameIndexInfo {

	@Id
	@SequenceGenerator(name = "ShapeFrameIndexInfo_SEQUENCE", sequenceName = "ShapeFrameIndexInfo_SEQUENCE")
	@GeneratedValue(generator = "ShapeFrameIndexInfo_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private Long taskMarkRecordId;

	@Column
	private Long fileId;

	@Column
	private Long startIndex;

	@Column
	private Long endIndex;

	@Column
	private String shapeName;

	public Long getId() {
		return id;
	}

	public Long getFileId() {
		return fileId;
	}

	public Long getStartIndex() {
		return startIndex;
	}

	public Long getEndIndex() {
		return endIndex;
	}

	public String getShapeName() {
		return shapeName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public void setStartIndex(Long startIndex) {
		this.startIndex = startIndex;
	}

	public void setEndIndex(Long endIndex) {
		this.endIndex = endIndex;
	}

	public void setShapeName(String shapeName) {
		this.shapeName = shapeName;
	}

	public Long getTaskMarkRecordId() {
		return taskMarkRecordId;
	}

	public void setTaskMarkRecordId(Long taskMarkRecordId) {
		this.taskMarkRecordId = taskMarkRecordId;
	}
}

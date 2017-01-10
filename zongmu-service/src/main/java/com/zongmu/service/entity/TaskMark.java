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
public class TaskMark implements Serializable{

    private static final long serialVersionUID = 5485538804359280059L;

    @Id
    @SequenceGenerator(name = "TASKMARK_SEQUENCE", sequenceName = "TASKMARK_SEQUENCE")
    @GeneratedValue(generator = "TASKMARK_SEQUENCE",strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private Long taskRecordId;
    
    private Long colorTagId;
    
    private String color;
    
    @Transient
    private ColorTag colorTag;
    
    @Transient
    private List<TaskItemFileMark> taskItemFileMarks = new ArrayList<>();
    
    @Transient
    private List<MarkShape> markShapes = new ArrayList<>();

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

    public List<MarkShape> getMarkShapes() {
        return markShapes;
    }

    public void setMarkShapes(List<MarkShape> markShapes) {
        this.markShapes = markShapes;
    }

	public List<TaskItemFileMark> getTaskItemFileMarks() {
		return taskItemFileMarks;
	}

	public void setTaskItemFileMarks(List<TaskItemFileMark> taskItemFileMarks) {
		this.taskItemFileMarks = taskItemFileMarks;
	}

	public Long getColorTagId() {
		return colorTagId;
	}

	public void setColorTagId(Long colorTagId) {
		this.colorTagId = colorTagId;
	}

	public ColorTag getColorTag() {
		return colorTag;
	}

	public void setColorTag(ColorTag colorTag) {
		this.colorTag = colorTag;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}

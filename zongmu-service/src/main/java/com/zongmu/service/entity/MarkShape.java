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

import com.zongmu.service.dto.mark.ShapeType;

@Entity
@Table
public class MarkShape implements Serializable {

    private static final long serialVersionUID = -1514205904197908287L;

    @Id
    @SequenceGenerator(name = "MARKSHAPE_SEQUENCE", sequenceName = "MARKSHAPE_SEQUENCE")
    @GeneratedValue(generator = "MARKSHAPE_SEQUENCE",strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Long taskMarkId;
    
    @Column
    private Long taskItemFileMarkId;

    @Column
    private ShapeType type;

    @Column
    private Integer sideCount;

    @Column
    private String shapeId;

    @Column
    private String name;

    @Column
    private String color;

    @Column
    private Integer startIndex;

    @Column
    private Integer endIndex;

    @Transient
    private List<Timeline> timelines = new ArrayList<>();

    @Transient
    private List<MarkTag> tags = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskMarkId() {
        return taskMarkId;
    }

    public void setTaskMarkId(Long taskMarkId) {
        this.taskMarkId = taskMarkId;
    }

    public ShapeType getType() {
        return type;
    }

    public void setType(ShapeType type) {
        this.type = type;
    }

    public Integer getSideCount() {
        return sideCount;
    }

    public void setSideCount(Integer sideCount) {
        this.sideCount = sideCount;
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

    public List<Timeline> getTimelines() {
        return timelines;
    }

    public void setTimelines(List<Timeline> timelines) {
        this.timelines = timelines;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public List<MarkTag> getTags() {
        return tags;
    }

    public void setTags(List<MarkTag> tags) {
        this.tags = tags;
    }

	public Long getTaskItemFileMarkId() {
		return taskItemFileMarkId;
	}

	public void setTaskItemFileMarkId(Long taskItemFileMarkId) {
		this.taskItemFileMarkId = taskItemFileMarkId;
	}

}

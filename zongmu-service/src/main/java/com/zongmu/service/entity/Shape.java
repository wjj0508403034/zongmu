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

import com.zongmu.service.dto.mark.ShapeType;

@Entity
@Table
public class Shape implements Serializable {

    private static final long serialVersionUID = -4625168780243512834L;

    @Id
    @SequenceGenerator(name = "SHAPE_SEQUENCE", sequenceName = "SHAPE_SEQUENCE")
    @GeneratedValue(generator = "SHAPE_SEQUENCE",strategy = GenerationType.AUTO)
    private long id;

    private String shapeId;
    private String shapeName;
    private int sideCount;
    private ShapeType type;
    private String color;
    private Long markId;
    private Long timelineId;

    @Transient
    private List<Point> points = new ArrayList<>();

    @Transient
    private List<MarkTag> tags = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ShapeType getType() {
        return type;
    }

    public void setType(ShapeType type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<MarkTag> getTags() {
        return tags;
    }

    public void setTags(List<MarkTag> tags) {
        this.tags = tags;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public String getShapeName() {
        return shapeName;
    }

    public void setShapeName(String shapeName) {
        this.shapeName = shapeName;
    }

    public int getSideCount() {
        return sideCount;
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }

    public Long getMarkId() {
        return markId;
    }

    public void setMarkId(Long markId) {
        this.markId = markId;
    }

    public Long getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(Long timelineId) {
        this.timelineId = timelineId;
    }

}

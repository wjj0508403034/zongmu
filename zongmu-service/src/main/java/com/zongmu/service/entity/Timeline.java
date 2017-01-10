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
public class Timeline implements Serializable {

    private static final long serialVersionUID = 5021524270229185884L;

    @Id
    @SequenceGenerator(name = "TIMELINE_SEQUENCE", sequenceName = "TIMELINE_SEQUENCE")
    @GeneratedValue(generator = "TIMELINE_SEQUENCE",strategy = GenerationType.AUTO)
    private long id;

    @Column
    private Long markShapeId;

    @Column
    private Integer frameIndex;

    @Transient
    private List<Point> points = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public Long getMarkShapeId() {
        return markShapeId;
    }

    public void setMarkShapeId(Long markShapeId) {
        this.markShapeId = markShapeId;
    }

    public Integer getFrameIndex() {
        return frameIndex;
    }

    public void setFrameIndex(Integer frameIndex) {
        this.frameIndex = frameIndex;
    }

}

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
public class Point implements Serializable {
    private static final long serialVersionUID = 7637399350649722468L;

    @Id
    @SequenceGenerator(name = "POINT_SEQUENCE", sequenceName = "POINT_SEQUENCE")
    @GeneratedValue(generator = "POINT_SEQUENCE",strategy = GenerationType.AUTO)
    private long id;

    private float x;
    private float y;
    private Long shapeId;
    private Long timelineId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Long getShapeId() {
        return shapeId;
    }

    public void setShapeId(Long shapeId) {
        this.shapeId = shapeId;
    }

    public Long getTimelineId() {
        return timelineId;
    }

    public void setTimelineId(Long timelineId) {
        this.timelineId = timelineId;
    }


}

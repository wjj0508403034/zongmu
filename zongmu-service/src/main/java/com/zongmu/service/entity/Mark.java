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
public class Mark implements Serializable {

    private static final long serialVersionUID = 2488321447761973778L;

    @Id
    @SequenceGenerator(name = "MARK_SEQUENCE", sequenceName = "MARK_SEQUENCE")
    @GeneratedValue(generator = "MARK_SEQUENCE",strategy = GenerationType.AUTO)
    private Long id;

    private String taskRecordNo;
    private float currentTime;
    private float frameIndex;

    @Transient
    private List<Shape> shapes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public void setShapes(List<Shape> shapes) {
        this.shapes = shapes;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(float currentTime) {
        this.currentTime = currentTime;
    }

    public float getFrameIndex() {
        return frameIndex;
    }

    public void setFrameIndex(float frameIndex) {
        this.frameIndex = frameIndex;
    }

    public String getTaskRecordNo() {
        return taskRecordNo;
    }

    public void setTaskRecordNo(String taskRecordNo) {
        this.taskRecordNo = taskRecordNo;
    }

}

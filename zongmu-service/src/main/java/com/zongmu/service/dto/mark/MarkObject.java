package com.zongmu.service.dto.mark;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.service.dto.DTOObject;

public class MarkObject extends DTOObject {

    private static final long serialVersionUID = 6427347029085547762L;

    private Long id;
    private String taskItemNo;
    private String taskRecordNo;
    private float currentTime;
    private float frameIndex;
    private List<ShapeObject> shapes = new ArrayList<>();

    public List<ShapeObject> getShapes() {
        return shapes;
    }

    public void setShapes(List<ShapeObject> shapes) {
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

    public String getTaskItemNo() {
        return taskItemNo;
    }

    public void setTaskItemNo(String taskItemNo) {
        this.taskItemNo = taskItemNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskRecordNo() {
        return taskRecordNo;
    }

    public void setTaskRecordNo(String taskRecordNo) {
        this.taskRecordNo = taskRecordNo;
    }
}

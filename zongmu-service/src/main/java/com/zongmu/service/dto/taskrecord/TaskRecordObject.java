package com.zongmu.service.dto.taskrecord;

import org.joda.time.DateTime;

import com.zongmu.service.dto.DTOObject;
import com.zongmu.service.dto.task.TaskItemObject;
import com.zongmu.service.entity.TaskMark;

public class TaskRecordObject extends DTOObject {
    private static final long serialVersionUID = 7300922197348493826L;

    private long id;

    private String taskRecordNo;

    private String taskItemNo;

    private Long userId;

    private String userName;

    private DateTime startTime;

    private DateTime endTime;
    
    private String reviewRecordNo;

    private TaskRecordStatus status;

    private TaskItemObject taskItem;

    private TaskMark taskMark;
    
    private int point;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public TaskRecordStatus getStatus() {
        return status;
    }

    public void setStatus(TaskRecordStatus status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTaskItemNo() {
        return taskItemNo;
    }

    public void setTaskItemNo(String taskItemNo) {
        this.taskItemNo = taskItemNo;
    }

    public TaskItemObject getTaskItem() {
        return taskItem;
    }

    public void setTaskItem(TaskItemObject taskItem) {
        this.taskItem = taskItem;
    }

    public String getTaskRecordNo() {
        return taskRecordNo;
    }

    public void setTaskRecordNo(String taskRecordNo) {
        this.taskRecordNo = taskRecordNo;
    }

    public TaskMark getTaskMark() {
        return taskMark;
    }

    public void setTaskMark(TaskMark taskMark) {
        this.taskMark = taskMark;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getReviewRecordNo() {
        return reviewRecordNo;
    }

    public void setReviewRecordNo(String reviewRecordNo) {
        this.reviewRecordNo = reviewRecordNo;
    }
}

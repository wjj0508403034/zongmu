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
public class TaskRefAssetTag implements Serializable {

    private static final long serialVersionUID = 2840000330097895757L;

    @Id
    @SequenceGenerator(name = "TASKREFASSETTAG_SEQUENCE", sequenceName = "TASKREFASSETTAG_SEQUENCE")
    @GeneratedValue(generator = "TASKREFASSETTAG_SEQUENCE",strategy = GenerationType.AUTO)
    private Long id;

    private Long taskId;
    private Long assetTagId;
    private String assetTagName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getAssetTagId() {
        return assetTagId;
    }

    public void setAssetTagId(Long assetTagId) {
        this.assetTagId = assetTagId;
    }

    public String getAssetTagName() {
        return assetTagName;
    }

    public void setAssetTagName(String assetTagName) {
        this.assetTagName = assetTagName;
    }
}

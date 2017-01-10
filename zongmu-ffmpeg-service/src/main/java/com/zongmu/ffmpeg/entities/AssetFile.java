package com.zongmu.ffmpeg.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.joda.time.DateTime;

@Entity
@Table
public class AssetFile {
    
    @Id
    @SequenceGenerator(name = "ASSETFILE_SEQUENCE", sequenceName = "ASSETFILE_SEQUENCE")
    @GeneratedValue(generator = "ASSETFILE_SEQUENCE", strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private String assetNo;
    
    @Column
    private String assetFileNo;
    
    @Column
    private String fileName;
    
    @Column
    private String fileType;
    
    @Column
    private Long fileSize;
    
    @Column
    private DateTime createTime;
    
    @Column
    private DateTime updateTime;
    
    @Column
    private AssetFileStatus assetFileStatus;
    
    @Column
    private int fps;
    
    @Column
    private float height;
    
    @Column
    private float width;
    
    @Column
    private float duration;
    
    @Column
    private AssetType assetType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getAssetFileNo() {
        return assetFileNo;
    }

    public void setAssetFileNo(String assetFileNo) {
        this.assetFileNo = assetFileNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    public AssetFileStatus getAssetFileStatus() {
        return assetFileStatus;
    }

    public void setAssetFileStatus(AssetFileStatus assetFileStatus) {
        this.assetFileStatus = assetFileStatus;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }
}

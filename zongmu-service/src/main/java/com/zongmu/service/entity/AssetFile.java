package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;
import com.zongmu.service.dto.asset.AssetFileStatus;
import com.zongmu.service.dto.asset.AssetType;

@Entity
@Table
public class AssetFile implements Serializable {

	private static final long serialVersionUID = 6246337478068275797L;

	@Id
	@SequenceGenerator(name = "ASSETFILE_SEQUENCE", sequenceName = "ASSETFILE_SEQUENCE")
	@GeneratedValue(generator = "ASSETFILE_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;
	private String assetNo;
	private String assetFileNo;
	private String fileName;
	private String fileType;
	private Long fileSize;
	private DateTime createTime;
	private DateTime updateTime;
	private AssetFileStatus assetFileStatus;
	private int fps;
	private float height;
	private float width;
	private float duration;
	private Long frames;
	
	@Column
	private AssetType assetType;

	@Transient
	private Asset asset;

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

	public DateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(DateTime updateTime) {
		this.updateTime = updateTime;
	}

	public String getAssetFileNo() {
		return assetFileNo;
	}

	public void setAssetFileNo(String assetFileNo) {
		this.assetFileNo = assetFileNo;
	}

	public AssetFileStatus getAssetFileStatus() {
		return assetFileStatus;
	}

	public void setAssetFileStatus(AssetFileStatus assetFileStatus) {
		this.assetFileStatus = assetFileStatus;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
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

	public Long getFrames() {
		return frames;
	}

	public void setFrames(Long frames) {
		this.frames = frames;
	}

}

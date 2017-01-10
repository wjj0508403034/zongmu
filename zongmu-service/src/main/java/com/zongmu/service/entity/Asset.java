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

import org.joda.time.DateTime;

import com.zongmu.service.dto.asset.AssetFileStatus;
import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.asset.Status;

@Entity
@Table
public class Asset implements Serializable {

	private static final long serialVersionUID = 7449761442785249340L;

	@Id
	@SequenceGenerator(name = "ASSET_SEQUENCE", sequenceName = "ASSET_SEQUENCE")
	@GeneratedValue(generator = "ASSET_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private DateTime recordTime;

	@Column
	private int timeOfDay;

	@Column
	private Float recordLength;
	
	@Column
	private String name;

	@Column
	private String memo;

	@Column
	private Status status;

	@Column
	private String assetNo;

	@Column
	private AssetType assetType;

	@Column
	private DateTime createTime;

	@Column
	private Long weatherTagId;

	@Column
	private Long roadTagId;

	@Transient
	private AssetTag weatherTag;

	@Transient
	private AssetTag roadTag;

	@Transient
	private List<AssetFile> assetFiles = new ArrayList<>();
	
	@Transient
	private List<Asset2AssetViewTag> viewTags = new ArrayList<>();

	@Transient
	private boolean canCompress;

	@Transient
	private boolean canCreateTask;
	
	@Transient
	private String ftpFolder;
	
	@Transient
	private String fullFtpFolder;
	
	@Transient
	private Long pictureCount;

	public boolean isCanCompress() {
		if (this.getAssetType() == AssetType.PICTURE) {
			return false;
		}

		if (this.getAssetType() == AssetType.SINGLE && this.getAssetFiles().size() == 1) {
			return this.getAssetFiles().get(0).getAssetFileStatus() == AssetFileStatus.FTPUPLOADSUCCESS;
		}

		if (this.getAssetType() == AssetType.FOUR && this.getAssetFiles().size() == 4) {
			boolean res = true;
			for (AssetFile assetFile : this.getAssetFiles()) {
				if (assetFile.getAssetFileStatus() != AssetFileStatus.FTPUPLOADSUCCESS) {
					res = false;
					break;
				}
			}
			return res;
		}

		return false;
	}

	public boolean isCanCreateTask() {
		if (this.getAssetType() == AssetType.PICTURE) {
			return true;
		}
		if (this.getAssetType() == AssetType.SINGLE && this.getAssetFiles().size() == 1) {
			return this.getAssetFiles().get(0).getAssetFileStatus() == AssetFileStatus.COMPRESSSUCCESS;
		}

		if (this.getAssetType() == AssetType.FOUR && this.getAssetFiles().size() == 4) {
			boolean res = true;
			for (AssetFile assetFile : this.getAssetFiles()) {
				if (assetFile.getAssetFileStatus() != AssetFileStatus.COMPRESSSUCCESS) {
					res = false;
					break;
				}
			}
			return res;
		}

		return false;
	}

	public void setCanCreateTask(boolean canCreateTask) {

	}

	public void setCanCompress(boolean canCompress) {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getAssetNo() {
		return assetNo;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public List<AssetFile> getAssetFiles() {
		return assetFiles;
	}

	public void setAssetFiles(List<AssetFile> assetFiles) {
		this.assetFiles = assetFiles;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public Float getRecordLength() {
		return recordLength;
	}

	public void setRecordLength(Float recordLength) {
		this.recordLength = recordLength;
	}

	public DateTime getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(DateTime recordTime) {
		this.recordTime = recordTime;
	}

	public int getTimeOfDay() {
		return timeOfDay;
	}

	public void setTimeOfDay(int timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public Long getRoadTagId() {
		return roadTagId;
	}

	public void setRoadTagId(Long roadTagId) {
		this.roadTagId = roadTagId;
	}

	public Long getWeatherTagId() {
		return weatherTagId;
	}

	public void setWeatherTagId(Long weatherTagId) {
		this.weatherTagId = weatherTagId;
	}

	public AssetTag getWeatherTag() {
		return weatherTag;
	}

	public void setWeatherTag(AssetTag weatherTag) {
		this.weatherTag = weatherTag;
	}

	public AssetTag getRoadTag() {
		return roadTag;
	}

	public void setRoadTag(AssetTag roadTag) {
		this.roadTag = roadTag;
	}

	public List<Asset2AssetViewTag> getViewTags() {
		return viewTags;
	}

	public void setViewTags(List<Asset2AssetViewTag> viewTags) {
		this.viewTags = viewTags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFtpFolder() {
		return ftpFolder;
	}

	public void setFtpFolder(String ftpFolder) {
		this.ftpFolder = ftpFolder;
	}

	public String getFullFtpFolder() {
		return fullFtpFolder;
	}

	public void setFullFtpFolder(String fullFtpFolder) {
		this.fullFtpFolder = fullFtpFolder;
	}

	public Long getPictureCount() {
		return pictureCount;
	}

	public void setPictureCount(Long pictureCount) {
		this.pictureCount = pictureCount;
	}

}

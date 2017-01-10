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
import org.springframework.data.domain.Page;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.mark.ShapeType;
import com.zongmu.service.dto.task.TaskStatus;
import com.zongmu.service.dto.task.TaskType;

@Entity
@Table
public class Task implements Serializable {

	private static final long serialVersionUID = -707546554133989144L;

	@Id
	@SequenceGenerator(name = "TASK_SEQUENCE", sequenceName = "TASK_SEQUENCE")
	@GeneratedValue(generator = "TASK_SEQUENCE", strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String assetNo;

	@Column
	private String taskNo;

	@Column
	private String taskName;

	@Column
	private TaskType taskType;

	@Column
	private ShapeType shapeType;

	@Column
	private AssetType assetType;

	@Column
	private int sideCount;

	@Column
	private int timeInterval;

	@Column
	private int point;

	@Column
	private Long userId;

	@Column
	private DateTime createTime;

	@Column
	private DateTime updateTime;

	@Column
	private int subTotal;

	@Column
	private String memo;

	@Column
	private TaskStatus taskStatus;

	@Column
	private boolean top;

	@Column
	private int priority = 3;

	@Column
	private Long algorithmId;

	@Column
	private boolean showHome = true;

	@Column
	private Long weatherTagId;

	@Column
	private Long roadTagId;

	@Column
	private int timeOfDay;
	
	@Column
	private Float videoLength;

	@Transient
	private AssetTag weatherTag;

	@Transient
	private AssetTag roadTag;

	@Transient
	private Page<TaskItem> taskItems;

	@Transient
	private Asset asset;

	@Transient
	private String algorithmName;
	
	@Transient
	private List<TaskXViewTag> viewTags = new ArrayList<>();
	
	@Transient
	private List<Task2AssetViewTag> assetViewTags = new ArrayList<>();
	
	@Transient
	private String ftpFolder;
	
	@Transient
	private Integer fps;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public int getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(int subTotal) {
		this.subTotal = subTotal;
	}

	public String getAssetNo() {
		return assetNo;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public ShapeType getShapeType() {
		return shapeType;
	}

	public void setShapeType(ShapeType shapeType) {
		this.shapeType = shapeType;
	}

	public int getSideCount() {
		return sideCount;
	}

	public void setSideCount(int sideCount) {
		this.sideCount = sideCount;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(int timeInterval) {
		this.timeInterval = timeInterval;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public List<Task2AssetViewTag> getAssetViewTags() {
		return assetViewTags;
	}

	public void setAssetViewTags(List<Task2AssetViewTag> assetViewTags) {
		this.assetViewTags = assetViewTags;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Page<TaskItem> getTaskItems() {
		return taskItems;
	}

	public void setTaskItems(Page<TaskItem> taskItems) {
		this.taskItems = taskItems;
	}

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Long getAlgorithmId() {
		return algorithmId;
	}

	public void setAlgorithmId(Long algorithmId) {
		this.algorithmId = algorithmId;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public boolean isShowHome() {
		return showHome;
	}

	public void setShowHome(boolean showHome) {
		this.showHome = showHome;
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

	public Long getWeatherTagId() {
		return weatherTagId;
	}

	public void setWeatherTagId(Long weatherTagId) {
		this.weatherTagId = weatherTagId;
	}

	public Long getRoadTagId() {
		return roadTagId;
	}

	public void setRoadTagId(Long roadTagId) {
		this.roadTagId = roadTagId;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public int getTimeOfDay() {
		return timeOfDay;
	}

	public void setTimeOfDay(int timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public Float getVideoLength() {
		return videoLength;
	}

	public void setVideoLength(Float videoLength) {
		this.videoLength = videoLength;
	}

	public List<TaskXViewTag> getViewTags() {
		return viewTags;
	}

	public void setViewTags(List<TaskXViewTag> viewTags) {
		this.viewTags = viewTags;
	}

	public String getFtpFolder() {
		return ftpFolder;
	}

	public void setFtpFolder(String ftpFolder) {
		this.ftpFolder = ftpFolder;
	}

	public Integer getFps() {
		return fps;
	}

	public void setFps(Integer fps) {
		this.fps = fps;
	}
}

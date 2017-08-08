package com.zongmu.service.dto.newexport.pic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.newexport.AbstractXML;
import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.Asset2AssetViewTag;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.AssetViewTagItem;
import com.zongmu.service.entity.ColorTag;
import com.zongmu.service.entity.Tag;
import com.zongmu.service.entity.TagItem;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskItemFile;
import com.zongmu.service.entity.TaskItemXViewTag;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.entity.ViewTag;
import com.zongmu.service.entity.ViewTagItem;
import com.zongmu.service.entity.mark.TaskMarkGroup;
import com.zongmu.service.entity.mark.TaskMarkPoint;
import com.zongmu.service.entity.mark.TaskMarkRecord;
import com.zongmu.service.entity.mark.TaskMarkRecordRefTag;
import com.zongmu.service.util.ZStringUtil;

public class TaskXml extends AbstractXML {

	private Asset asset;

	private Task task;

	private Algorithm algorithm;

	private List<Asset2AssetViewTag> videoTags = new ArrayList<>();

	private List<TaskItem> taskItems = new ArrayList<>();

	private ConcurrentHashMap<String, AssetFile> assetFileMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<Long, ViewTag> viewTagMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<Long, ViewTagItem> viewTagItemMap = new ConcurrentHashMap<>();

	/*
	 * key: TaskItemFileId
	 */
	private ConcurrentHashMap<Long, List<TaskMarkGroup>> markGroupMap = new ConcurrentHashMap<>();

	/*
	 * key: taskMarkRecordId
	 */
	private ConcurrentHashMap<Long, List<TaskMarkRecordRefTag>> refTagMap = new ConcurrentHashMap<>();

	/*
	 * key: id
	 */
	private ConcurrentHashMap<Long, TaskMarkRecord> taskmarkrecordMap = new ConcurrentHashMap<>();

	/*
	 * key: id
	 */
	private ConcurrentHashMap<Long, Tag> tagMap = new ConcurrentHashMap<>();

	/*
	 * key: id
	 */
	private ConcurrentHashMap<Long, TagItem> tagItemMap = new ConcurrentHashMap<>();

	/*
	 * key: id
	 */
	private ConcurrentHashMap<Long, ColorTag> colorTagMap = new ConcurrentHashMap<>();

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public List<Asset2AssetViewTag> getVideoTags() {
		return videoTags;
	}

	public void setVideoTags(List<Asset2AssetViewTag> videoTags) {
		this.videoTags = videoTags;
	}

	public List<TaskItem> getTaskItems() {
		return taskItems;
	}

	public void setTaskItems(List<TaskItem> taskItems) {
		this.taskItems = taskItems;
	}

	public ConcurrentHashMap<String, AssetFile> getAssetFileMap() {
		return assetFileMap;
	}

	public void setAssetFileMap(ConcurrentHashMap<String, AssetFile> assetFileMap) {
		this.assetFileMap = assetFileMap;
	}

	public ConcurrentHashMap<Long, ViewTag> getViewTagMap() {
		return viewTagMap;
	}

	public void setViewTagMap(ConcurrentHashMap<Long, ViewTag> viewTagMap) {
		this.viewTagMap = viewTagMap;
	}

	public ConcurrentHashMap<Long, ViewTagItem> getViewTagItemMap() {
		return viewTagItemMap;
	}

	public void setViewTagItemMap(ConcurrentHashMap<Long, ViewTagItem> viewTagItemMap) {
		this.viewTagItemMap = viewTagItemMap;
	}

	@Override
	public Element toXml(Document doc) {
		Element root = doc.createElement("annotation_property");
		root.appendChild(this.createElement(doc, "name", this.asset.getName()));
		root.appendChild(this.createElement(doc, "assetNo", this.asset.getAssetNo()));
		root.appendChild(this.createElement(doc, "assetType", this.asset.getAssetType().toString()));

		root.appendChild(this.createElement(doc, "taskName", this.task.getTaskName()));
		root.appendChild(this.createElement(doc, "taskNo", this.task.getTaskNo()));
		root.appendChild(this.createElement(doc, "taskType", this.task.getTaskType().toString()));
		root.appendChild(this.createElement(doc, "shapeType", this.task.getShapeType().toString()));
		root.appendChild(this.createElement(doc, "shapeSideCount", ZStringUtil.intToStr(this.task.getSideCount())));

		root.appendChild(this.getAlgorithmElement(doc));

		if (this.asset.getAssetType() != AssetType.PICTURE) {
			root.appendChild(this.getChannelsElement(doc));
		}

		root.appendChild(this.getVideoPropertiesElement(doc));

		root.appendChild(this.getTaskItemsElement(doc));
		return root;
	}

	private Element getChannelsElement(Document doc) {
		Element root = doc.createElement("channels");

		for (AssetFile assetFile : this.assetFileMap.values()) {
			root.appendChild(this.getChannelElement(doc, assetFile));
		}

		return root;
	}

	private Element getChannelElement(Document doc, AssetFile assetFile) {
		Element root = doc.createElement(this.getChannelName(assetFile));
		root.appendChild(this.createElement(doc, "fileName", assetFile.getFileName()));
		root.appendChild(this.createElement(doc, "assetFileNo", assetFile.getAssetFileNo()));
		root.appendChild(this.createElement(doc, "fileSize", ZStringUtil.longToStr(assetFile.getFileSize())));
		root.appendChild(this.createElement(doc, "height", ZStringUtil.floatToStr(assetFile.getHeight())));
		root.appendChild(this.createElement(doc, "width", ZStringUtil.floatToStr(assetFile.getWidth())));
		root.appendChild(this.createElement(doc, "fps", ZStringUtil.intToStr(assetFile.getFps())));
		root.appendChild(this.createElement(doc, "duration", ZStringUtil.floatToStr(assetFile.getDuration())));
		return root;
	}

	private String getChannelName(AssetFile assetFile) {
		if (StringUtils.equalsIgnoreCase(assetFile.getFileName(), "front.avi")) {
			return "channel0";
		}
		if (StringUtils.equalsIgnoreCase(assetFile.getFileName(), "left.avi")) {
			return "channel1";
		}
		if (StringUtils.equalsIgnoreCase(assetFile.getFileName(), "right.avi")) {
			return "channel2";
		}
		if (StringUtils.equalsIgnoreCase(assetFile.getFileName(), "rear.avi")) {
			return "channel3";
		}

		return "channel0";
	}

	private String getChannelName(TaskItemFile taskItemFile) {
		AssetFile assetFile = this.assetFileMap.get(taskItemFile.getAssetFileNo());
		if (assetFile != null) {
			return this.getChannelName(assetFile);
		}

		return "";
	}

	private Element getAlgorithmElement(Document doc) {
		Element root = doc.createElement("algorithm");
		root.appendChild(this.createElement(doc, "id", ZStringUtil.longToStr(this.algorithm.getId())));
		root.appendChild(this.createElement(doc, "name", this.algorithm.getName()));
		return root;
	}

	private Element getVideoPropertiesElement(Document doc) {
		Element root = doc.createElement("videoProperties");
		for (int index = 0; index < this.videoTags.size(); index++) {
			root.appendChild(this.getVideoPropertyElement(doc, this.videoTags.get(index), index));
		}
		return root;
	}

	private Element getVideoPropertyElement(Document doc, Asset2AssetViewTag tag, int index) {
		Element root = doc.createElement("property" + index);
		root.appendChild(this.createElement(doc, "nameId", ZStringUtil.longToStr(tag.getAssetViewTagId())));
		root.appendChild(this.createElement(doc, "name", tag.getViewTag().getName()));
		root.appendChild(this.createElement(doc, "valueId", ZStringUtil.longToStr(tag.getAssetViewTagItemId())));

		String value = "";
		if (tag.getAssetViewTagItemId() != null) {
			for (AssetViewTagItem item : tag.getViewTag().getItems()) {
				if (item.getId().equals(tag.getAssetViewTagItemId())) {
					value = item.getName();
					break;
				}
			}
		}

		root.appendChild(this.createElement(doc, "value", value));
		return root;
	}

	private Element getTaskItemsElement(Document doc) {
		Element root = doc.createElement("pictures");
		int counter = 0;
		for (TaskItem taskItem : this.taskItems) {
			for (TaskItemFile taskItemFile : taskItem.getTaskItemFiles()) {
				root.appendChild(this.getTaskItemElement(doc, taskItem, taskItemFile, counter));
				counter++;
			}
		}
		return root;
	}

	private Element getTaskItemElement(Document doc, TaskItem taskItem, TaskItemFile taskItemFile, int index) {
		Element root = doc.createElement("picture" + index);
		AssetFile assetFile = this.assetFileMap.get(taskItemFile.getAssetFileNo());
		root.appendChild(this.createElement(doc, "fileName", this.getPictureName(taskItem, assetFile)));
		root.appendChild(this.createElement(doc, "assetFileNo", assetFile.getAssetFileNo()));
		root.appendChild(this.createElement(doc, "fileSize", ZStringUtil.longToStr(assetFile.getFileSize())));
		root.appendChild(this.createElement(doc, "width", ZStringUtil.floatToStr(assetFile.getWidth())));
		root.appendChild(this.createElement(doc, "height", ZStringUtil.floatToStr(assetFile.getHeight())));
		if (this.asset.getAssetType() != AssetType.PICTURE) {
			root.appendChild(this.createElement(doc, "channel", this.getChannelName(taskItemFile)));
			root.appendChild(this.createElement(doc, "frameIndex", this.getPictureFrameIndex(taskItem, assetFile)));
		}
		root.appendChild(this.getTaskItemPropertiesElement(doc, taskItem));
		root.appendChild(this.getShapeObjectsElement(doc, taskItemFile));
		return root;
	}

	private Element getShapeObjectsElement(Document doc, TaskItemFile taskItemFile) {
		Element root = doc.createElement("objects");
		List<TaskMarkGroup> groups = this.markGroupMap.get(taskItemFile.getId());
		if (groups != null && groups.size() > 0) {
			int counter = 0;
			for (TaskMarkGroup group : groups) {
				root.appendChild(this.getShapeObjectElement(doc, group, taskItemFile, counter));
				counter++;
			}
		}
		return root;
	}

	private Element getShapeObjectElement(Document doc, TaskMarkGroup group, TaskItemFile taskItemFile, int index) {
		Element root = doc.createElement("object" + index);

		TaskMarkRecord taskMarkRecord = this.taskmarkrecordMap.get(group.getTaskMarkRecordId());

		root.appendChild(this.createElement(doc, "name", taskMarkRecord.getName()));
		root.appendChild(this.createElement(doc, "color", taskMarkRecord.getColor()));
		root.appendChild(this.createElement(doc, "colorTagId", ZStringUtil.longToStr(taskMarkRecord.getColorTagId())));
		root.appendChild(this.createElement(doc, "taskItemNo", taskItemFile.getTaskItemNo()));
		root.appendChild(this.getShapeObjectPropertiesElement(doc, group));
		root.appendChild(this.getPointsElement(doc, group));
		return root;
	}

	private Element getShapeObjectPropertiesElement(Document doc, TaskMarkGroup group) {
		Element root = doc.createElement("properties");
		List<TaskMarkRecordRefTag> refTags = this.refTagMap.get(group.getTaskMarkRecordId());
		int counter = 0;
		for (TaskMarkRecordRefTag refTag : refTags) {
			TagItem tagItem = this.tagItemMap.get(refTag.getTagItemId());
			if (tagItem != null) {
				root.appendChild(this.getShapeObjectPropertyElement(doc, tagItem, counter));
				counter++;
			}
		}
		return root;
	}

	private Element getShapeObjectPropertyElement(Document doc, TagItem tagItem, int index) {
		Element root = doc.createElement("property" + index);
		root.appendChild(this.createElement(doc, "nameId", ZStringUtil.longToStr(tagItem.getTagId())));

		String name = "";
		Tag tag = this.tagMap.get(tagItem.getTagId());
		if (tag != null) {
			name = tag.getName();
		}
		root.appendChild(this.createElement(doc, "name", name));
		root.appendChild(this.createElement(doc, "valueId", ZStringUtil.longToStr(tagItem.getId())));
		root.appendChild(this.createElement(doc, "value", tagItem.getValue()));
		return root;
	}

	private Element getPointsElement(Document doc, TaskMarkGroup group) {
		Element root = doc.createElement("points");
		int counter = 0;
		for (TaskMarkPoint point : group.getPoints()) {
			root.appendChild(this.getPointElement(doc, point, counter));
			counter++;
		}
		return root;
	}

	private Element getPointElement(Document doc, TaskMarkPoint point, int index) {
		Element root = doc.createElement("point" + index);

		root.appendChild(this.createElement(doc, "x", ZStringUtil.floatToStr(point.getX())));
		root.appendChild(this.createElement(doc, "y", ZStringUtil.floatToStr(point.getY())));
		return root;
	}

	private String getPictureName(TaskItem taskItem, AssetFile assetFile) {
		if (this.asset.getAssetType() != AssetType.PICTURE) {
			return String.format("%s_%d.jpg", assetFile.getFileName(), taskItem.getOrderNo());
		}

		return assetFile.getFileName();
	}

	private String getPictureFrameIndex(TaskItem taskItem, AssetFile assetFile) {
		long frameIndex = 1l * this.task.getTimeInterval() * taskItem.getOrderNo() * assetFile.getFps();
		return ZStringUtil.longToStr(frameIndex);
	}

	private Element getTaskItemPropertiesElement(Document doc, TaskItem taskItem) {
		Element root = doc.createElement("properties");
		int counter = 0;
		for (TaskItemXViewTag tag : taskItem.getViewTags()) {
			root.appendChild(this.getTaskItemPropertyElement(doc, tag, counter));
			counter++;
		}
		return root;
	}

	private Element getTaskItemPropertyElement(Document doc, TaskItemXViewTag tag, int index) {
		Element root = doc.createElement("property" + index);

		root.appendChild(this.createElement(doc, "nameId", ZStringUtil.longToStr(tag.getViewTagId())));

		ViewTag viewTag = this.viewTagMap.get(tag.getViewTagId());
		String name = "";
		if (viewTag != null) {
			name = viewTag.getName();
		}

		root.appendChild(this.createElement(doc, "name", name));
		root.appendChild(this.createElement(doc, "valueId", ZStringUtil.longToStr(tag.getViewTagItemId())));

		String value = "";
		ViewTagItem viewTagItem = this.viewTagItemMap.get(tag.getViewTagItemId());
		if (viewTagItem != null) {
			value = viewTagItem.getName();
		}

		root.appendChild(this.createElement(doc, "value", value));

		return root;
	}

	public void setAssetFiles(List<AssetFile> assetFiles) {
		for (AssetFile assetFile : assetFiles) {
			this.assetFileMap.put(assetFile.getAssetFileNo(), assetFile);
		}
	}

	public void setViewTags(List<ViewTag> viewTags) {
		for (ViewTag viewTag : viewTags) {
			this.viewTagMap.put(viewTag.getId(), viewTag);
		}
	}

	public void setViewTagItems(List<ViewTagItem> viewTagItems) {
		for (ViewTagItem viewTagItem : viewTagItems) {
			this.viewTagItemMap.put(viewTagItem.getId(), viewTagItem);
		}
	}

	public void addGroups(List<TaskMarkGroup> groups, TaskRecord taskRecord) {
		for (TaskMarkGroup group : groups) {
			if (group.getTaskItemFileId() == null) {
				group.setTaskItemFileId(getTaskItemFileId(taskRecord));
			}

			if (group.getTaskItemFileId() != null) {
				List<TaskMarkGroup> temp = this.markGroupMap.get(group.getTaskItemFileId());
				if (temp == null) {
					temp = new ArrayList<>();
					this.markGroupMap.put(group.getTaskItemFileId(), temp);
				}
				temp.add(group);
			}
		}
	}

	private Long getTaskItemFileId(TaskRecord taskRecord) {
		if (taskRecord.getTaskItemId() == null) {
			return null;
		}

		for (TaskItem taskItem : this.taskItems) {
			if (taskRecord.getTaskItemId().longValue() == taskItem.getId()) {
				if (taskItem.getTaskItemFiles().size() > 0) {
					return taskItem.getTaskItemFiles().get(0).getId();
				}
			}
		}

		return null;
	}

	public void addRefTags(List<TaskMarkRecordRefTag> refTags) {
		for (TaskMarkRecordRefTag refTag : refTags) {
			List<TaskMarkRecordRefTag> temp = this.refTagMap.get(refTag.getTaskMarkRecordId());
			if (temp == null) {
				temp = new ArrayList<>();
				this.refTagMap.put(refTag.getTaskMarkRecordId(), temp);
			}
			temp.add(refTag);
		}
	}

	public void addTaskMarkRecords(List<TaskMarkRecord> taskMarkRecords) {
		for (TaskMarkRecord record : taskMarkRecords) {
			this.taskmarkrecordMap.put(record.getId(), record);
		}
	}

	public void setTags(List<Tag> tags) {
		for (Tag tag : tags) {
			this.tagMap.put(tag.getId(), tag);
		}
	}

	public void addTagItems(List<TagItem> tagItems) {
		for (TagItem tagitem : tagItems) {
			this.tagItemMap.put(tagitem.getId(), tagitem);
		}
	}

}

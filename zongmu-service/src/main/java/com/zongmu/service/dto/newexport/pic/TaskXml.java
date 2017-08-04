package com.zongmu.service.dto.newexport.pic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zongmu.service.dto.newexport.AbstractXML;
import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.Asset2AssetViewTag;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.AssetViewTagItem;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskItemFile;
import com.zongmu.service.entity.TaskItemXViewTag;
import com.zongmu.service.entity.ViewTag;
import com.zongmu.service.entity.ViewTagItem;
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

		root.appendChild(this.getVideoPropertiesElement(doc));

		root.appendChild(this.getTaskItemsElement(doc));
		return root;
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
				if (item.getId() == tag.getAssetViewTagItemId()) {
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
		root.appendChild(this.createElement(doc, "fileName", assetFile.getFileName()));
		root.appendChild(this.createElement(doc, "assetFileNo", assetFile.getAssetFileNo()));
		root.appendChild(this.createElement(doc, "fileSize", ZStringUtil.longToStr(assetFile.getFileSize())));
		root.appendChild(this.createElement(doc, "width", ZStringUtil.floatToStr(assetFile.getWidth())));
		root.appendChild(this.createElement(doc, "height", ZStringUtil.floatToStr(assetFile.getHeight())));

		root.appendChild(this.getTaskItemPropertiesElement(doc, taskItem));
		return root;
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

}

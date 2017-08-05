package com.zongmu.service.dto.newexport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.entity.Asset2AssetViewTag;
import com.zongmu.service.entity.AssetViewTagItem;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.util.ZStringUtil;

public class EAssetObject extends AbstractXML {

	private String assetName;
	private String assetNo;
	private AssetType assetType;
	private ETaskObject task;
	private Map<String, EAssetFileObject> fileMap = new HashMap<>();
	private List<Asset2AssetViewTag> videoTags = new ArrayList<>();
	private Integer fps;
	private List<TaskItem> taskItems = new ArrayList<>();

	public String getAssetName() {
		return assetName;
	}

	public String getAssetNo() {
		return assetNo;
	}

	public ETaskObject getTask() {
		return task;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public void setTask(ETaskObject task) {
		this.task = task;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	@Override
	public Element toXml(Document doc) {
		Element root = doc.createElement("annotation_property");
		root.appendChild(this.createElement(doc, "name", this.getAssetName()));
		root.appendChild(this.createElement(doc, "assetNo", this.getAssetNo()));
		root.appendChild(this.createElement(doc, "assetType", this.getAssetType().toString()));
		if (this.getTask() != null) {
			for (Element element : this.getTask().toSimpleXml(doc)) {
				if (element != null) {
					root.appendChild(element);
				}
			}
		}

		root.appendChild(this.toVideoTagsElement(doc));
		Element channels = doc.createElement("channels");
		root.appendChild(channels);
		for (EAssetFileObject file : this.getFileMap().values()) {
			channels.appendChild(file.toXml(doc));
		}

		if (this.getTask() != null) {
			for (Element element : this.getTask().toXml(doc, this)) {
				if (element != null) {
					root.appendChild(element);
				}
			}
		}
		return root;
	}

	private Element toVideoTagsElement(Document doc) {
		Element root = doc.createElement("videoProperties");
		for (int index = 0; index < this.getVideoTags().size(); index++) {
			Element tagEle = doc.createElement("property" + index);
			root.appendChild(tagEle);
			Asset2AssetViewTag tag = this.getVideoTags().get(index);
			tagEle.appendChild(this.createElement(doc, "nameId", ZStringUtil.longToStr(tag.getAssetViewTagId())));
			tagEle.appendChild(this.createElement(doc, "name", tag.getViewTag().getName()));
			tagEle.appendChild(this.createElement(doc, "valueId", ZStringUtil.longToStr(tag.getAssetViewTagItemId())));
			if (tag.getAssetViewTagItemId() != null) {
				for (AssetViewTagItem item : tag.getViewTag().getItems()) {
					if (item.getId().equals(tag.getAssetViewTagItemId())) {
						tagEle.appendChild(this.createElement(doc, "value", item.getName()));
						break;
					}
				}

			} else {
				tagEle.appendChild(this.createElement(doc, "value", ""));
			}

		}
		return root;
	}

	public Map<String, EAssetFileObject> getFileMap() {
		return fileMap;
	}

	public void setFileMap(Map<String, EAssetFileObject> fileMap) {
		this.fileMap = fileMap;
	}

	public List<Asset2AssetViewTag> getVideoTags() {
		return videoTags;
	}

	public void setVideoTags(List<Asset2AssetViewTag> videoTags) {
		this.videoTags = videoTags;
	}

	public Integer getFps() {
		return fps;
	}

	public void setFps(Integer fps) {
		this.fps = fps;
	}

	public List<TaskItem> getTaskItems() {
		return taskItems;
	}

	public void setTaskItems(List<TaskItem> taskItems) {
		this.taskItems = taskItems;
	}
}

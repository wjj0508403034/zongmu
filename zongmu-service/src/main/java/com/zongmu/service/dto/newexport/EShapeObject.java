package com.zongmu.service.dto.newexport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.util.ZStringUtil;

public class EShapeObject extends AbstractXML {

	private Long id;
	private String name;
	private String color;
	private Long colorId;
	private String taskItemNo;
	private int taskItemOrderNo;
	private Map<String, List<EChannelObject>> channelMap = new HashMap<>();
	private List<EShapeTagObject> tags = new ArrayList<>();
	private TaskItem taskItem;
	private Task task;
	private FrameIndexRange endRange;
	private FrameIndexRange startRange;
	private boolean merged;

	private List<EShapeObject> mergedShapeObjects = new ArrayList<>();

	public boolean matchTaskItemStart() {
		for (String fileName : this.getChannelMap().keySet()) {
			List<EFrameInfoObject> frameInfos = this.frameInfoMap.get(fileName);
			for (EFrameInfoObject frameInfo : frameInfos) {
				if (frameInfo.isInStart()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean matchTaskItemEnd() {
		for (String fileName : this.getChannelMap().keySet()) {
			List<EFrameInfoObject> frameInfos = this.frameInfoMap.get(fileName);
			for (EFrameInfoObject frameInfo : frameInfos) {
				if (frameInfo.isInEnd()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean match(EShapeObject shapeObj) {
		for (String fileName : this.getChannelMap().keySet()) {
			for (EChannelObject preChannelObj : this.getChannelMap().get(fileName)) {
				if (shapeObj.getChannelMap().containsKey(fileName)) {
					for (EChannelObject nextChannelObj : shapeObj.getChannelMap().get(fileName)) {
						if (preChannelObj.match(nextChannelObj)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public Map<String, List<EChannelObject>> getChannelMap() {
		return channelMap;
	}

	public void setChannelMap(Map<String, List<EChannelObject>> channelMap) {
		this.channelMap = channelMap;
	}

	private Map<String, List<EFrameInfoObject>> frameInfoMap = new HashMap<>();

	public void merge(EShapeObject shapeObject) {
		this.mergedShapeObjects.add(shapeObject);
		for (String fileName : shapeObject.getChannelMap().keySet()) {
			List<EChannelObject> channelObjects = new ArrayList<>();
			if(this.channelMap.containsKey(fileName)){
				for (EChannelObject channelObj : this.channelMap.get(fileName)) {
					channelObjects.add(channelObj);
				}
			}
			
			if(shapeObject.getChannelMap().containsKey(fileName)){
				for (EChannelObject channelObj : shapeObject.getChannelMap().get(fileName)) {
					channelObjects.add(channelObj);
				}
			}
			

			this.channelMap.put(fileName, channelObjects);
		}

		for (String fileName : shapeObject.getFrameInfoMap().keySet()) {
			List<EFrameInfoObject> newFrameInfos = new ArrayList<>();
			if(this.frameInfoMap.containsKey(fileName)){
				for (EFrameInfoObject frameInfoObject : this.frameInfoMap.get(fileName)) {
					newFrameInfos.add(frameInfoObject);
				}
			}
			
			if(shapeObject.getFrameInfoMap().containsKey(fileName)){
				for (EFrameInfoObject frameInfoObject : shapeObject.getFrameInfoMap().get(fileName)) {
					newFrameInfos.add(frameInfoObject);
				}
			}


			int index = 0;
			while (index < newFrameInfos.size()) {
				if (index + 1 < newFrameInfos.size()) {
					if (newFrameInfos.get(index).isOverlap(newFrameInfos.get(index + 1))) {
						newFrameInfos.get(index).merge(newFrameInfos.get(index + 1));
						newFrameInfos.remove(index + 1);
						continue;
					}
				}
				index++;
			}

			this.frameInfoMap.put(fileName, newFrameInfos);
		}
	}

	public void addFrameInfo(EFrameInfoObject obj) {
		List<EFrameInfoObject> list = frameInfoMap.get(obj.getFileId());
		if (list == null) {
			list = new ArrayList<>();
			frameInfoMap.put(obj.getTaskItemFileName(), list);
		}

		list.add(obj);
	}

	public void addChannel(EChannelObject channel) {
		List<EChannelObject> list = channelMap.get(channel.getTaskItemFileId());
		if (list == null) {
			list = new ArrayList<>();
			channelMap.put(channel.getFileName().toLowerCase(), list);
		}
		list.add(channel);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public Element toXml(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getTagName(String fileName) {
		if (StringUtils.equalsIgnoreCase(fileName, "front.avi")) {
			return "channel0";
		}

		if (StringUtils.equalsIgnoreCase(fileName, "left.avi")) {
			return "channel1";
		}
		if (StringUtils.equalsIgnoreCase(fileName, "right.avi")) {
			return "channel2";
		}
		if (StringUtils.equalsIgnoreCase(fileName, "rear.avi")) {
			return "channel3";
		}

		return "channel0";
	}

	private static void toShapeXml(Document doc, Element root, EAssetObject asset, EShapeObject shapeObject) {
		root.appendChild(shapeObject.createElement(doc, "name", shapeObject.getName()));
		root.appendChild(shapeObject.createElement(doc, "color", shapeObject.getColor()));
		root.appendChild(shapeObject.createElement(doc, "colorTagId", ZStringUtil.longToStr(shapeObject.getColorId())));
		root.appendChild(shapeObject.createElement(doc, "taskItemNo", shapeObject.getTaskItemNo()));
		root.appendChild(shapeObject.createElement(doc, "orderNo", Integer.toString(shapeObject.getTaskItemOrderNo())));

		Element propertiesElement = doc.createElement("properties");
		root.appendChild(propertiesElement);
		for (int pIndex = 0; pIndex < shapeObject.getTags().size(); pIndex++) {
			propertiesElement.appendChild(shapeObject.getTags().get(pIndex).toXml(pIndex, doc));
		}

		root.appendChild(shapeObject.startRange.toXml(doc));
		root.appendChild(shapeObject.endRange.toXml(doc));

		Element channels = doc.createElement("channels");
		root.appendChild(channels);
		if(shapeObject.getChannelMap().size() > 1){
			if(shapeObject.getChannelMap().containsKey("front.avi")){
				doHandle(doc,channels,asset,"front.avi",shapeObject);
			}
			
			if(shapeObject.getChannelMap().containsKey("left.avi")){
				doHandle(doc,channels,asset,"left.avi",shapeObject);
			}
			
			if(shapeObject.getChannelMap().containsKey("right.avi")){
				doHandle(doc,channels,asset,"right.avi",shapeObject);
			}
			
			if(shapeObject.getChannelMap().containsKey("rear.avi")){
				doHandle(doc,channels,asset,"rear.avi",shapeObject);
			}
		}else{
			for (String fileName : shapeObject.getChannelMap().keySet()) {
				doHandle(doc,channels,asset,fileName,shapeObject);
			}
		}
		
//		for (String fileName : shapeObject.getChannelMap().keySet()) {
//			String tagName = shapeObject.getTagName(fileName);
//			Element channelElement = doc.createElement(tagName);
//			channels.appendChild(channelElement);
//
//			Element frameElement = doc.createElement("frameSections");
//			channelElement.appendChild(frameElement);
//			List<EFrameInfoObject> frameInfos = shapeObject.frameInfoMap.get(fileName);
//			if (frameInfos != null) {
//				for (int frameIndex = 0; frameIndex < frameInfos.size(); frameIndex++) {
//					frameElement.appendChild(frameInfos.get(frameIndex).toXml(frameIndex, doc));
//				}
//			}
//
//			Element geoElement = doc.createElement("geometries");
//			channelElement.appendChild(geoElement);
//			List<EChannelObject> channelObjs = shapeObject.getChannelMap().get(fileName);
//			List<EGeometryObject> newEGeometryObjects = new ArrayList<EGeometryObject>();
//			for (EChannelObject channelObj : channelObjs) {
//				newEGeometryObjects.addAll(channelObj.getGeometries());
//			}
//			
//			Collections.sort(newEGeometryObjects);
//			for (Element ele : shapeObject.toXmlList(doc, asset, frameInfos, newEGeometryObjects)) {
//				geoElement.appendChild(ele);
//			}
//		}
	}
	
	private static void doHandle(Document doc, Element channels,EAssetObject asset,String fileName,EShapeObject shapeObject){
		String tagName = shapeObject.getTagName(fileName);
		Element channelElement = doc.createElement(tagName);
		channels.appendChild(channelElement);

		Element frameElement = doc.createElement("frameSections");
		channelElement.appendChild(frameElement);
		List<EFrameInfoObject> frameInfos = shapeObject.frameInfoMap.get(fileName);
		if (frameInfos != null) {
			for (int frameIndex = 0; frameIndex < frameInfos.size(); frameIndex++) {
				frameElement.appendChild(frameInfos.get(frameIndex).toXml(frameIndex, doc));
			}
		}

		Element geoElement = doc.createElement("geometries");
		channelElement.appendChild(geoElement);
		List<EChannelObject> channelObjs = shapeObject.getChannelMap().get(fileName);
		List<EGeometryObject> newEGeometryObjects = new ArrayList<EGeometryObject>();
		for (EChannelObject channelObj : channelObjs) {
			newEGeometryObjects.addAll(channelObj.getGeometries());
		}
		
		Collections.sort(newEGeometryObjects);
		for (Element ele : shapeObject.toXmlList(doc, asset, frameInfos, newEGeometryObjects)) {
			geoElement.appendChild(ele);
		}
	}

	public Node toXml(int objectIndex, Document doc, EAssetObject asset) {
		Element root = doc.createElement("object" + objectIndex);
		toShapeXml(doc, root, asset, this);
		if (this.mergedShapeObjects.size() > 0) {
			Element mergedObjects = doc.createElement("mergedObjects");
			root.appendChild(mergedObjects);
			for (int index = 0; index < this.mergedShapeObjects.size(); index++) {
				Element objEle = doc.createElement("object" + index);
				mergedObjects.appendChild(objEle);
				toShapeXml(doc, objEle, asset, this.mergedShapeObjects.get(index));
			}
		}

		// root.appendChild(this.createElement(doc, "name", this.getName()));
		// root.appendChild(this.createElement(doc, "color", this.getColor()));
		// root.appendChild(this.createElement(doc, "colorTagId",
		// ZStringUtil.longToStr(this.getColorId())));
		// root.appendChild(this.createElement(doc, "taskItemNo",
		// this.getTaskItemNo()));
		// root.appendChild(this.createElement(doc, "orderNo",
		// Integer.toString(this.getTaskItemOrderNo())));
		//
		// Element propertiesElement = doc.createElement("properties");
		// root.appendChild(propertiesElement);
		// for (int pIndex = 0; pIndex < this.getTags().size(); pIndex++) {
		// propertiesElement.appendChild(this.getTags().get(pIndex).toXml(pIndex,
		// doc));
		// }
		//
		// root.appendChild(this.startRange.toXml(doc));
		// root.appendChild(this.endRange.toXml(doc));
		//
		// Element channels = doc.createElement("channels");
		// root.appendChild(channels);
		//
		// for (Long fileId : this.getChannelMap().keySet()) {
		// String tagName = this.getTagName(fileId, asset);
		// Element channelElement = doc.createElement(tagName);
		// channels.appendChild(channelElement);
		//
		// Element frameElement = doc.createElement("frameSections");
		// channelElement.appendChild(frameElement);
		// List<EFrameInfoObject> frameInfos = this.frameInfoMap.get(fileId);
		// if (frameInfos != null) {
		// for (int frameIndex = 0; frameIndex < frameInfos.size();
		// frameIndex++) {
		// frameElement.appendChild(frameInfos.get(frameIndex).toXml(frameIndex,
		// doc));
		// }
		// }
		//
		// Element geoElement = doc.createElement("geometries");
		// channelElement.appendChild(geoElement);
		// List<EChannelObject> channelObjs = this.getChannelMap().get(fileId);
		// List<EGeometryObject> newEGeometryObjects = new
		// ArrayList<EGeometryObject>();
		// for (EChannelObject channelObj : channelObjs) {
		// newEGeometryObjects.addAll(channelObj.getGeometries());
		// }
		//
		// for (Element ele : this.toXmlList(doc, asset, frameInfos,
		// newEGeometryObjects)) {
		// geoElement.appendChild(ele);
		// }
		// }
		return root;
	}

	public List<Element> toXmlList(Document doc, EAssetObject asset, List<EFrameInfoObject> frameInfos,
			List<EGeometryObject> newEGeometryObjects) {
		List<Element> elements = new ArrayList<>();
		for (int index = 0; index < newEGeometryObjects.size(); index++) {
			elements.add(newEGeometryObjects.get(index).toXml(index, doc, asset, frameInfos));
		}
		return elements;
	}

	public Map<String, List<EFrameInfoObject>> getFrameInfoMap() {
		return frameInfoMap;
	}

	public void setFrameInfoMap(Map<String, List<EFrameInfoObject>> frameInfoMap) {
		this.frameInfoMap = frameInfoMap;
	}

	public Long getColorId() {
		return colorId;
	}

	public void setColorId(Long colorId) {
		this.colorId = colorId;
	}

	public List<EShapeTagObject> getTags() {
		return tags;
	}

	public void setTags(List<EShapeTagObject> tags) {
		this.tags = tags;
	}

	public TaskItem getTaskItem() {
		return taskItem;
	}

	public void setTaskItem(TaskItem taskItem) {
		this.taskItem = taskItem;
	}

	public FrameIndexRange getEndRange() {
		return endRange;
	}

	public void setEndRange(FrameIndexRange endRange) {
		this.endRange = endRange;
	}

	public FrameIndexRange getStartRange() {
		return startRange;
	}

	public void setStartRange(FrameIndexRange startRange) {
		this.startRange = startRange;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getTaskItemNo() {
		return taskItemNo;
	}

	public void setTaskItemNo(String taskItemNo) {
		this.taskItemNo = taskItemNo;
	}

	public int getTaskItemOrderNo() {
		return taskItemOrderNo;
	}

	public void setTaskItemOrderNo(int taskItemOrderNo) {
		this.taskItemOrderNo = taskItemOrderNo;
	}

	public boolean isMerged() {
		return merged;
	}

	public void setMerged(boolean merged) {
		this.merged = merged;
	}

}

package com.zongmu.service.dto.newexport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zongmu.service.dto.mark.ShapeType;
import com.zongmu.service.dto.task.TaskType;
import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.Tag;
import com.zongmu.service.util.ZStringUtil;

public class ETaskObject extends AbstractXML {

	private String taskName;
	private String taskNo;
	private TaskType taskType;
	private ShapeType shapeType;
	private int sideCount;
	private Algorithm algorithm;
	private Map<Long, ETaskItemFileObject> fileMap = new HashMap<>();
	private Map<String, ETaskViewTagObject> tagMap = new HashMap<>();
	private List<Tag> markTags = new ArrayList<>();
	private Map<Integer, List<EShapeObject>> shapeObjectMap = new HashMap<>();

	public void addShapeObject(Integer taskItemOrderNo, EShapeObject shapeObject) {
		if (!shapeObjectMap.containsKey(taskItemOrderNo)) {
			shapeObjectMap.put(taskItemOrderNo, new ArrayList<EShapeObject>());
		}

		shapeObjectMap.get(taskItemOrderNo).add(shapeObject);
	}

	public Map<Long, ETaskItemFileObject> getFileMap() {
		return fileMap;
	}

	public String getTaskName() {
		return taskName;
	}

	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	@Override
	public Element toXml(Document doc) {
		return null;
	}

	public List<Element> toSimpleXml(Document doc) {
		List<Element> elements = new ArrayList<>();
		elements.add(this.createElement(doc, "taskName", this.getTaskName()));
		elements.add(this.createElement(doc, "taskNo", this.getTaskNo()));
		elements.add(this.createElement(doc, "taskType", this.getTaskType().toString()));
		elements.add(this.createElement(doc, "shapeType", this.getShapeType().toString()));
		if (this.getShapeType() == ShapeType.RECT) {
			elements.add(this.createElement(doc, "shapeSideCount", "4"));
		} else if (this.getShapeType() == ShapeType.POLYLINE) {
			elements.add(this.createElement(doc, "shapeSideCount", ZStringUtil.intToStr(this.sideCount)));
		}
		Element alElement = doc.createElement("algorithm");
		elements.add(alElement);
		alElement.appendChild(this.createElement(doc, "id", ZStringUtil.longToStr(this.getAlgorithm().getId())));
		alElement.appendChild(this.createElement(doc, "name", this.getAlgorithm().getName()));
		return elements;
	}

	public List<Element> toXml(Document doc, EAssetObject asset) {
		List<Element> elements = new ArrayList<>();
		elements.add(toViewTagXml(doc, asset));
		Element objects = doc.createElement("objects");
		elements.add(objects);

		List<EShapeObject> shapeObjects = new ArrayList<>();
		for (Integer taskItemOrderNo : this.getShapeObjectMap().keySet()) {
			if (this.getShapeObjectMap().containsKey(taskItemOrderNo + 1)) {
				mergeShapeObjects(this.getShapeObjectMap().get(taskItemOrderNo),
						this.getShapeObjectMap().get(taskItemOrderNo + 1));
			}

			for (EShapeObject shapeObject : this.getShapeObjectMap().get(taskItemOrderNo)) {
				if (!shapeObject.isMerged()) {
					shapeObjects.add(shapeObject);
				}
			}

		}

		for (int jindex = 0; jindex < shapeObjects.size(); jindex++) {
			objects.appendChild(shapeObjects.get(jindex).toXml(jindex, doc, asset));
		}

		return elements;
	}

	public void mergeShapeObjects(List<EShapeObject> preShapeList, List<EShapeObject> oldShapeList) {
		for (EShapeObject preShape : preShapeList) {
			if (preShape.matchTaskItemEnd()) {
				for (EShapeObject oldShape : oldShapeList) {
					if (oldShape.matchTaskItemStart()) {
						if (!oldShape.isMerged() && !preShape.isMerged() && preShape.match(oldShape)) {
							preShape.setMerged(true);
							oldShape.setMerged(true);
							oldShape.merge(preShape);
						}
					}
				}
			}
		}
		
		for (EShapeObject oldShape : oldShapeList) {
			oldShape.setMerged(false);
		}
	}

	private Element toViewTagXml(Document doc, EAssetObject asset) {
		Element element = doc.createElement("properties");
		int index = 0;
		for (String key : this.tagMap.keySet()) {
			ETaskViewTagObject tag = this.tagMap.get(key);
			element.appendChild(tag.toXml(index, doc, asset));
			index++;
		}

		return element;
	}

	public ShapeType getShapeType() {
		return shapeType;
	}

	public void setShapeType(ShapeType shapeType) {
		this.shapeType = shapeType;
	}

	public void setFileMap(Map<Long, ETaskItemFileObject> fileMap) {
		this.fileMap = fileMap;
	}

	public int getSideCount() {
		return sideCount;
	}

	public void setSideCount(int sideCount) {
		this.sideCount = sideCount;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public Map<String, ETaskViewTagObject> getTagMap() {
		return tagMap;
	}

	public void setTagMap(Map<String, ETaskViewTagObject> tagMap) {
		this.tagMap = tagMap;
	}

	public List<Tag> getMarkTags() {
		return markTags;
	}

	public void setMarkTags(List<Tag> markTags) {
		this.markTags = markTags;
	}

	public Map<Integer, List<EShapeObject>> getShapeObjectMap() {
		return shapeObjectMap;
	}

	public void setShapeObjectMap(Map<Integer, List<EShapeObject>> shapeObjectMap) {
		this.shapeObjectMap = shapeObjectMap;
	}

}

package com.zongmu.service.dto.newexport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zongmu.service.entity.Point;

public class EChannelObject extends AbstractXML implements
		Comparable<EChannelObject> {

	private Long taskItemFileId;
	private String fileName;
	private FrameIndexRange endRange;
	private FrameIndexRange startRange;
	private List<EGeometryObject> geometries = new ArrayList<>();

	public boolean match(EChannelObject nextChannelObj) {
		if (this.getGeometries().size() > 0
				&& nextChannelObj.getGeometries().size() > 0) {
			EGeometryObject preObj = this.getGeometries().get(
					this.getGeometries().size() - 1);
			EGeometryObject nextObj = this.getNextGeometryObject(
					preObj.getFrameIndex(), nextChannelObj.getGeometries());
			if (nextObj.getFrameIndex() < preObj.getFrameIndex()) {
				EGeometryObject temp = this.getPrevGeometryObject(nextObj.getFrameIndex(),
						this.getGeometries(), nextChannelObj.getGeometries()
								.get(0).getFrameIndex());
				if(temp != null){
					preObj = temp;
				}
			}
			if (preObj.isSame(nextObj)) {
				return true;
			}

		}
		return false;
	}

	private EGeometryObject getPrevGeometryObject(long frameIndex,
			List<EGeometryObject> geometries, long stopFrameIndex) {

		EGeometryObject nextObj = null;
		EGeometryObject prevObj = null;
		for(int index = geometries.size() - 1; index >= 0; index--){
			EGeometryObject current = geometries.get(index);
			if(current.getFrameIndex() == frameIndex){
				return current;
			}
			
			if(current.getFrameIndex() > frameIndex){
				nextObj = current;
			}else if(current.getFrameIndex() < frameIndex){
				prevObj = current;
				break;
			}
			
			if(current.getFrameIndex() < stopFrameIndex){
				if(index == geometries.size()){
					return current;
				}else{
					return geometries.get(index+1);
				}
			}
			
		}
		
		if(nextObj == null){
			return prevObj;
		}
		
		return nextObj;
	}

	private EGeometryObject getNextGeometryObject(long frameIndex,
			List<EGeometryObject> geometries) {
		EGeometryObject prevObj = null;
		EGeometryObject nextObj = null;

		for (int index = 0; index < geometries.size(); index++) {
			EGeometryObject current = geometries.get(index);
			if (current.getFrameIndex() > frameIndex) {
				if (prevObj == null) {
					return current;
				} else {
					nextObj = current;
					break;
				}
			} else if (current.getFrameIndex() == frameIndex) {
				return current;
			} else {
				prevObj = current;
			}
		}

		if (nextObj == null) {
			return prevObj;
		}

		return this.calcNewEGeometryObject(frameIndex, prevObj, nextObj);

	}

	private EGeometryObject calcNewEGeometryObject(long frameIndex,
			EGeometryObject prev, EGeometryObject next) {
		long delta = next.getFrameIndex() - prev.getFrameIndex();
		// if(delta <= 0){
		// return prev;
		// }

		float rate = (frameIndex - delta) * 1.0f / (delta * 1.0f) + 1.0f;

		EGeometryObject newObj = new EGeometryObject();
		newObj.setFrameIndex(frameIndex);

		for (EPointObject point : prev.getPoints()) {
			newObj.getPoints().add(point.zoom(rate));
		}

		return newObj;
	}

	public List<EGeometryObject> getGeometries() {
		return geometries;
	}

	public void setGeometries(List<EGeometryObject> geometries) {
		this.geometries = geometries;
	}

	public Long getTaskItemFileId() {
		return taskItemFileId;
	}

	public void setTaskItemFileId(Long taskItemFileId) {
		this.taskItemFileId = taskItemFileId;
	}

	@Override
	public Element toXml(Document doc) {
		return null;
	}

	public List<Element> toXmlList(Document doc, EAssetObject asset,
			List<EFrameInfoObject> frameInfos) {
		List<Element> elements = new ArrayList<>();
		for (int index = 0; index < this.getGeometries().size(); index++) {
			elements.add(this.geometries.get(index).toXml(index, doc, asset,
					frameInfos));
		}
		return elements;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public int compareTo(EChannelObject obj) {
		// if (StringUtils.equalsIgnoreCase(fileName, "front.avi")) {
		// tagName = "channel0";
		// }
		//
		// if (StringUtils.equalsIgnoreCase(fileName, "left.avi")) {
		// tagName = "channel1";
		// }
		// if (StringUtils.equalsIgnoreCase(fileName, "right.avi")) {
		// tagName = "channel2";
		// }
		// if (StringUtils.equalsIgnoreCase(fileName, "rear.avi")) {
		// tagName = "channel3";
		// }

		if (this.fileName == obj.fileName) {
			return 0;
		}

		if (StringUtils.equalsIgnoreCase(this.fileName, "front.avi")) {
			return -1;
		}

		if (StringUtils.equalsIgnoreCase(this.fileName, "left.avi")) {
			if (StringUtils.equalsIgnoreCase(this.fileName, "front.avi")) {
				return 1;
			}

			return -1;
		}

		if (StringUtils.equalsIgnoreCase(this.fileName, "right.avi")) {
			if (StringUtils.equalsIgnoreCase(this.fileName, "front.avi")
					|| StringUtils.equalsIgnoreCase(this.fileName, "left.avi")) {
				return 1;
			}

			return -1;
		}

		if (StringUtils.equalsIgnoreCase(this.fileName, "rear.avi")) {
			return 1;
		}

		return -1;
	}

	public FrameIndexRange getEndRange() {
		return endRange;
	}

	public FrameIndexRange getStartRange() {
		return startRange;
	}

	public void setEndRange(FrameIndexRange endRange) {
		this.endRange = endRange;
	}

	public void setStartRange(FrameIndexRange startRange) {
		this.startRange = startRange;
	}

}

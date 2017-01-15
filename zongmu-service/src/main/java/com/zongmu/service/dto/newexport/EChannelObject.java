package com.zongmu.service.dto.newexport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EChannelObject extends AbstractXML implements Comparable<EChannelObject> {

	private Long taskItemFileId;
	private String fileName;
	private FrameIndexRange endRange;
	private FrameIndexRange startRange;
	private List<EGeometryObject> geometries = new ArrayList<>();

	public boolean match(EChannelObject nextChannelObj) {
		if(this.getGeometries().size() > 0 && nextChannelObj.getGeometries().size() > 0){
			EGeometryObject preObj = this.getGeometries().get(this.getGeometries().size() - 1);
			EGeometryObject nextObj = nextChannelObj.getGeometries().get(0);
			if(preObj.isSame(nextObj)){
				return true;
			}
			
			
		}
//		for (EGeometryObject thisObj : this.getGeometries()) {
//			for (EGeometryObject obj : nextChannelObj.getGeometries()) {
//				if (thisObj.isSame(obj)) {
//					return true;
//				}
//			}
//		}
		return false;
	}
	
//	private boolean inRange(long index,FrameIndexRange startRange,FrameIndexRange endRange){
//		if(startRange.in(startIndex, endIndex))
//	}

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

	public List<Element> toXmlList(Document doc, EAssetObject asset, List<EFrameInfoObject> frameInfos) {
		List<Element> elements = new ArrayList<>();
		for (int index = 0; index < this.getGeometries().size(); index++) {
			elements.add(this.geometries.get(index).toXml(index, doc, asset, frameInfos));
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
//		if (StringUtils.equalsIgnoreCase(fileName, "front.avi")) {
//			tagName = "channel0";
//		}
//
//		if (StringUtils.equalsIgnoreCase(fileName, "left.avi")) {
//			tagName = "channel1";
//		}
//		if (StringUtils.equalsIgnoreCase(fileName, "right.avi")) {
//			tagName = "channel2";
//		}
//		if (StringUtils.equalsIgnoreCase(fileName, "rear.avi")) {
//			tagName = "channel3";
//		}
		
		if(this.fileName == obj.fileName){
			return 0;
		}
		
		if(StringUtils.equalsIgnoreCase(this.fileName, "front.avi")){
			return -1;
		}
		
		if(StringUtils.equalsIgnoreCase(this.fileName, "left.avi")){
			if(StringUtils.equalsIgnoreCase(this.fileName, "front.avi")){
				return 1;
			}
			
			return -1;
		}
		
		if(StringUtils.equalsIgnoreCase(this.fileName, "right.avi")){
			if(StringUtils.equalsIgnoreCase(this.fileName, "front.avi") || StringUtils.equalsIgnoreCase(this.fileName, "left.avi")){
				return 1;
			}
			
			return -1;
		}
		
		if(StringUtils.equalsIgnoreCase(this.fileName, "rear.avi")){
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

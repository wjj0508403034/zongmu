package com.zongmu.service.dto.newexport;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EChannelObject extends AbstractXML {

	private Long taskItemFileId;
	private String fileName;

	private List<EGeometryObject> geometries = new ArrayList<>();

	public boolean match(EChannelObject nextChannelObj) {
		for (EGeometryObject thisObj : this.getGeometries()) {
			for (EGeometryObject obj : nextChannelObj.getGeometries()) {
				if (thisObj.isSame(obj)) {
					return true;
				}
			}
		}
		return false;
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

}

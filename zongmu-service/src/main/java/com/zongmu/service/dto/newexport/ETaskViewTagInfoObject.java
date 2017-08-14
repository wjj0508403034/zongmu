package com.zongmu.service.dto.newexport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.zongmu.service.util.ZStringUtil;

public class ETaskViewTagInfoObject extends AbstractXML {

	private Long taskItemId;
	private Float startTime;
	private Float endTime;

	public Long getTaskItemId() {
		return taskItemId;
	}

	public Float getStartTime() {
		return startTime;
	}

	public Float getEndTime() {
		return endTime;
	}

	public void setTaskItemId(Long taskItemId) {
		this.taskItemId = taskItemId;
	}

	public void setStartTime(Float startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Float endTime) {
		this.endTime = endTime;
	}

	@Override
	public Element toXml(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	public Node toXml(int sectionIndex, Document doc, EAssetObject asset) {
		Element root = doc.createElement("frameSection" + sectionIndex);
		root.appendChild(this.createElement(doc, "startFrame", calcStartFrame(asset)));
		root.appendChild(this.createElement(doc, "endFrame", calcEndFrame(asset)));
		root.appendChild(this.createElement(doc, "startTime", ZStringUtil.intToStr(Math.round(this.getStartTime()))));
		root.appendChild(this.createElement(doc, "endTime", ZStringUtil.floatToStr(this.getEndTime())));
		return root;
	}

	private String calcEndFrame(EAssetObject asset) {
		Float value = this.getEndTime() * asset.getFps();
		return ZStringUtil.intToStr(value.intValue());
	}

	private String calcStartFrame(EAssetObject asset) {
		Float value = this.getStartTime() * asset.getFps();
		return ZStringUtil.intToStr(value.intValue());
	}

}

package com.zongmu.service.dto.newexport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EFrameInfoObject extends AbstractXML {

	private Long fileId;
	private String taskItemFileName;
	private Long startIndex;
	private Long endIndex;
	private FrameIndexRange startRange;
	private FrameIndexRange endRange;

	public Long getLength() {
		return this.endIndex - this.startIndex;
	}

	public boolean isOverlap(EFrameInfoObject frameInfoObject) {
		if (frameInfoObject.getLength() > this.getLength()) {
			if (this.startIndex > frameInfoObject.getEndIndex()) {
				return false;
			}

			if (this.endIndex < frameInfoObject.getStartIndex()) {
				return false;
			}

			return true;
		} else {
			if (frameInfoObject.getStartIndex() > endIndex) {
				return false;
			}

			if (frameInfoObject.getEndIndex() < startIndex) {
				return false;
			}

			return true;
		}
	}

	public void merge(EFrameInfoObject frameInfoObject) {
		if (frameInfoObject.getStartIndex() < this.startIndex) {
			this.startIndex = frameInfoObject.getStartIndex();
		}

		if (frameInfoObject.getEndIndex() > this.endIndex) {
			this.endIndex = frameInfoObject.getEndIndex();
		}
	}

	public boolean contains(FrameIndexRange range) {
		if (this.endIndex < range.getStart()) {
			return false;
		}

		if (this.startIndex > range.getEnd()) {
			return false;
		}

		return true;
	}

	public Long getFileId() {
		return fileId;
	}

	public Long getStartIndex() {
		return startIndex;
	}

	public Long getEndIndex() {
		return endIndex;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public void setStartIndex(Long startIndex) {
		this.startIndex = startIndex;
	}

	public void setEndIndex(Long endIndex) {
		this.endIndex = endIndex;
	}

	@Override
	public Element toXml(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	public Element toXml(int elementIndex, Document doc) {
		Element root = doc.createElement("frameSection" + elementIndex);
		root.appendChild(this.createElement(doc, "startFrame", String.valueOf(this.getStartIndex())));
		root.appendChild(this.createElement(doc, "endFrame", String.valueOf(this.getEndIndex())));
		root.appendChild(this.createElement(doc, "isInStart", String.valueOf(this.isInStart())));
		root.appendChild(this.createElement(doc, "isInEnd", String.valueOf(this.isInEnd())));
		return root;
	}

	public boolean isInStart() {
		return this.getStartRange().in(this.getStartIndex(),this.getEndIndex());
	}

	public boolean isInEnd() {
		return this.getEndRange().in(this.getStartIndex(),this.getEndIndex());
	}

	public FrameIndexRange getStartRange() {
		return startRange;
	}

	public FrameIndexRange getEndRange() {
		return endRange;
	}

	public void setStartRange(FrameIndexRange startRange) {
		this.startRange = startRange;
	}

	public void setEndRange(FrameIndexRange endRange) {
		this.endRange = endRange;
	}

	public String getTaskItemFileName() {
		return taskItemFileName;
	}

	public void setTaskItemFileName(String taskItemFileName) {
		this.taskItemFileName = taskItemFileName;
	}

}

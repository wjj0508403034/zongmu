package com.zongmu.service.dto.newexport;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EAssetFileObject extends AbstractXML {

	private String assetFileNo;
	private String fileName;
	private Long fileSize;
	private double height;
	private double width;
	private int fps;
	private double duration;

	public String getAssetFileNo() {
		return assetFileNo;
	}

	public String getFileName() {
		return fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public void setAssetFileNo(String assetFileNo) {
		this.assetFileNo = assetFileNo;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public String getTagName() {
		String tagName = null;
		if (StringUtils.equalsIgnoreCase(this.getFileName(), "front.avi")) {
			tagName = "channel0";
		}

		if (StringUtils.equalsIgnoreCase(this.getFileName(), "left.avi")) {
			tagName = "channel1";
		}
		if (StringUtils.equalsIgnoreCase(this.getFileName(), "right.avi")) {
			tagName = "channel2";
		}
		if (StringUtils.equalsIgnoreCase(this.getFileName(), "rear.avi")) {
			tagName = "channel3";
		}

		return tagName;
	}

	@Override
	public Element toXml(Document doc) {
		Element root = doc.createElement(this.getTagName());
		root.appendChild(this.createElement(doc, "fileName", this.getFileName()));
		root.appendChild(this.createElement(doc, "assetFileNo", this.getAssetFileNo()));
		root.appendChild(this.createElement(doc, "fileSize", this.getFileSize().toString()));
		root.appendChild(this.createElement(doc, "height", String.valueOf(this.getHeight())));
		root.appendChild(this.createElement(doc, "width", String.valueOf(this.getWidth())));
		root.appendChild(this.createElement(doc, "fps", String.valueOf(this.getFps())));
		root.appendChild(this.createElement(doc, "duration", String.valueOf(this.getDuration())));
		return root;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}
}

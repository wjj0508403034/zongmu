package com.zongmu.service.dto.export;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FrameObject extends XmlObject {

	private Long frameIndex;
	private List<FrameProperty> properties = new ArrayList<>();
	private List<ShapeObject> shapes = new ArrayList<>();
	private List<ChannelObject> channelObjects = new ArrayList<>();

	public FrameObject(Document doc, Long frameIndex) {
		super(doc);
		this.frameIndex = frameIndex;
	}

	@Override
	public Element toXml() {
		Element element = this.getDoc().createElement("frame" + frameIndex);
		Element props = this.getDoc().createElement("frames");
		element.appendChild(props);
		for (FrameProperty frameProperty : this.properties) {
			props.appendChild(frameProperty.toXml());
		}
		Element objs = this.getDoc().createElement("objects");
		element.appendChild(objs);
		for (ShapeObject obj : this.shapes) {
			objs.appendChild(obj.toXml());
		}
		return element;
	}

	public List<ChannelObject> getChannelObjects() {
		return channelObjects;
	}

	public void setChannelObjects(List<ChannelObject> channelObjects) {
		this.channelObjects = channelObjects;
	}

}

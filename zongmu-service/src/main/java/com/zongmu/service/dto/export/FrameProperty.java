package com.zongmu.service.dto.export;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FrameProperty extends XmlObject {

	private Long propertyIndex;

	public FrameProperty(Document doc, Long propertyIndex) {
		super(doc);
		this.propertyIndex = propertyIndex;
	}

	@Override
	public Element toXml() {
		Element element = this.getDoc().createElement("frame_property" + this.propertyIndex);
		return element;
	}

}

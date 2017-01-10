package com.zongmu.service.dto.export;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ShapeObject extends XmlObject {

	private Long objectIndex;

	public ShapeObject(Document doc, Long objectIndex) {
		super(doc);
		this.objectIndex = objectIndex;
	}

	@Override
	public Element toXml() {
		Element element = this.getDoc().createElement("object" + this.objectIndex);

		return element;
	}

}

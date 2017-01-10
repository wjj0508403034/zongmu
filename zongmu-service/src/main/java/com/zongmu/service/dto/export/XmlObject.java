package com.zongmu.service.dto.export;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class XmlObject {

	private Document doc;

	public XmlObject(Document doc) {
		this.doc = doc;
	}

	public abstract Element toXml();

	public Document getDoc() {
		return doc;
	}
}

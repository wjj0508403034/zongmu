package com.zongmu.service.dto.newexport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractXML implements IXml {

	protected Element createElement(Document doc, String name, String value) {
		Element ele = doc.createElement(name);
		ele.setTextContent(value);
		return ele;
	}
}

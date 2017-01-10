package com.zongmu.service.dto.newexport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zongmu.service.util.ZStringUtil;

public class EShapeTagObject extends AbstractXML {

	private Long tagId;
	private String tagName;
	private Long tagItemId;
	private String tagItemName;

	public Long getTagId() {
		return tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public Long getTagItemId() {
		return tagItemId;
	}

	public String getTagItemName() {
		return tagItemName;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public void setTagItemId(Long tagItemId) {
		this.tagItemId = tagItemId;
	}

	public void setTagItemName(String tagItemName) {
		this.tagItemName = tagItemName;
	}

	@Override
	public Element toXml(Document doc) {
		// Element root = doc.createElement(tagName)
		return null;
	}

	public Element toXml(int index, Document doc) {
		Element root = doc.createElement("property" + index);
		root.appendChild(this.createElement(doc, "nameId", ZStringUtil.longToStr(tagId)));
		root.appendChild(this.createElement(doc, "name", this.getTagName()));
		root.appendChild(this.createElement(doc, "valueId", ZStringUtil.longToStr(this.getTagItemId())));
		root.appendChild(this.createElement(doc, "value", this.getTagItemName()));
		return root;
	}

}

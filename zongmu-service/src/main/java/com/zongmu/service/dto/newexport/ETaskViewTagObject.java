package com.zongmu.service.dto.newexport;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.zongmu.service.entity.ViewTagItem;
import com.zongmu.service.util.ZStringUtil;

public class ETaskViewTagObject extends AbstractXML {

	private Long viewTagId;
	private String viewTagName;
	private ViewTagItem viewTagItem;

	private List<ETaskViewTagInfoObject> infos = new ArrayList<>();

	public Long getViewTagId() {
		return viewTagId;
	}

	public String getViewTagName() {
		return viewTagName;
	}

	public ViewTagItem getViewTagItem() {
		return viewTagItem;
	}

	public void setViewTagId(Long viewTagId) {
		this.viewTagId = viewTagId;
	}

	public void setViewTagName(String viewTagName) {
		this.viewTagName = viewTagName;
	}

	public void setViewTagItem(ViewTagItem viewTagItem) {
		this.viewTagItem = viewTagItem;
	}

	@Override
	public Element toXml(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ETaskViewTagInfoObject> getInfos() {
		return infos;
	}

	public void setInfos(List<ETaskViewTagInfoObject> infos) {
		this.infos = infos;
	}

	public Node toXml(int index, Document doc, EAssetObject asset) {
		Element root = doc.createElement("property" + index);
		root.appendChild(this.createElement(doc, "nameId", ZStringUtil.longToStr(this.getViewTagId())));
		root.appendChild(this.createElement(doc, "name", this.getViewTagName()));
		if (this.getViewTagItem() != null) {
			root.appendChild(this.createElement(doc, "valueId", ZStringUtil.longToStr(this.getViewTagItem().getId())));
			root.appendChild(this.createElement(doc, "value", this.getViewTagItem().getName()));
		} else {
			root.appendChild(this.createElement(doc, "valueId", ""));
			root.appendChild(this.createElement(doc, "value", ""));
		}

		Element sections = doc.createElement("frameSections");
		root.appendChild(sections);
		for (int sectionIndex = 0; sectionIndex < this.getInfos().size(); sectionIndex++) {
			sections.appendChild(this.getInfos().get(sectionIndex).toXml(sectionIndex,doc,asset));
		}
		return root;
	}

}

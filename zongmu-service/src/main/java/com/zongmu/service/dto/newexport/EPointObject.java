package com.zongmu.service.dto.newexport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EPointObject extends AbstractXML {

	private final static float DeltaValue = 80f;
	private float x;
	private float y;
	private boolean isMatch;

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public boolean isSame(EPointObject point) {
		float deltaX = point.getX() - this.getX();
		float deltaY = point.getY() - this.getY();

		if (deltaX <= DeltaValue && deltaX >= -DeltaValue) {
			if (deltaY <= DeltaValue && deltaY >= -DeltaValue) {
				return true;
			}
		}

		return false;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

	public Node toXml(int pointIndex, Document doc, EAssetObject asset) {
		Element root = doc.createElement("point" + pointIndex);
		root.appendChild(this.createElement(doc, "x", String.valueOf(this.getX())));
		root.appendChild(this.createElement(doc, "y", String.valueOf(this.getY())));
		return root;
	}

	@Override
	public Element toXml(Document doc) {
		return null;
	}
}

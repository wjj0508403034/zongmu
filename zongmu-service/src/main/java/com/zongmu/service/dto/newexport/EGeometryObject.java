package com.zongmu.service.dto.newexport;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zongmu.service.dto.mark.ShapeType;

public class EGeometryObject extends AbstractXML implements Comparable<EGeometryObject> {

	private long frameIndex;
	private int geometryType;
	private int tag;

	private List<EPointObject> points = new ArrayList<>();

	public boolean isSame(EGeometryObject pObject) {
		if (pObject.getPoints().size() != this.getPoints().size()) {
			return false;
		}

		for (EPointObject pPoint : pObject.getPoints()) {
			if (!pPoint.isMatch()) {
				boolean isMatch = false;
				for (EPointObject point : this.getPoints()) {
					if (!point.isMatch() && !pPoint.isMatch()) {
						if (pPoint.isSame(point)) {
							pPoint.setMatch(true);
							point.setMatch(true);
							isMatch = true;
						}
					}
				}

				if (!isMatch) {
					return false;
				}
			}
		}

		return true;
	}

	public long getFrameIndex() {
		return frameIndex;
	}

	public int getGeometryType() {
		return geometryType;
	}

	public List<EPointObject> getPoints() {
		return points;
	}

	public void setFrameIndex(long frameIndex) {
		this.frameIndex = frameIndex;
	}

	public void setGeometryType(int geometryType) {
		this.geometryType = geometryType;
	}

	public void setPoints(List<EPointObject> points) {
		this.points = points;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	@Override
	public Element toXml(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	public Element toXml(int objectIndex, Document doc, EAssetObject asset, List<EFrameInfoObject> frameInfos) {
		Element root = doc.createElement("geometry" + objectIndex);
		root.appendChild(this.createElement(doc, "frameIndex", String.valueOf(this.getFrameIndex())));
		root.appendChild(this.createElement(doc, "geometryType", String.valueOf(this.calcGeometryType(frameInfos))));
		Element points = doc.createElement("points");
		root.appendChild(points);
		List<EPointObject> newPoints = this.removeDuplicatePoints(this.getPoints(), asset);
		for (int pointIndex = 0; pointIndex < newPoints.size(); pointIndex++) {
			points.appendChild(newPoints.get(pointIndex).toXml(pointIndex, doc, asset));
		}
		return root;
	}

	private List<EPointObject> removeDuplicatePoints(List<EPointObject> originalPoints, EAssetObject asset) {
		if (originalPoints.size() <= 2) {
			return originalPoints;
		}

		if (asset.getTask().getShapeType() == ShapeType.RECT) {
			return originalPoints.subList(0, 2);
		}

		int index = originalPoints.size() - 1;
		while (index >= 2) {
			EPointObject lastPoint = originalPoints.get(index);
			EPointObject perviousPoint = originalPoints.get(index - 1);
			if (lastPoint.isEquals(perviousPoint)) {
				originalPoints.remove(index);
				index--;
			} else {
				break;
			}
		}

		return originalPoints;
	}

	private int calcGeometryType(List<EFrameInfoObject> frameInfos) {
		for (EFrameInfoObject info : frameInfos) {
			if (this.getFrameIndex() == info.getStartIndex()) {
				return 0;
			}

			if (this.getFrameIndex() == info.getEndIndex()) {
				return 2;
			}

			if (info.getStartIndex() < this.getFrameIndex() && this.getFrameIndex() < info.getEndIndex()) {
				return 1;
			}
		}

		return -1;
	}

	@Override
	public int compareTo(EGeometryObject obj) {
		if (this.frameIndex < obj.frameIndex) {
			return -1;
		}

		if (this.frameIndex == obj.frameIndex) {
			return 0;
		}

		return 1;
	}
}

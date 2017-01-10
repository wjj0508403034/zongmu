package com.zongmu.service.dto.newexport;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;

public class FrameIndexRange extends AbstractXML {

	private long start;
	private long end;
	private boolean isStart;

	public FrameIndexRange(Task task, TaskItem taskItem, boolean isStart) {
		this.isStart = isStart;
		if (this.isStart) {
			Float startValue = new Float(task.getFps() * taskItem.getStartTime());
			Float endValue = new Float(task.getFps() * (taskItem.getStartTime() + 0.5f));
			this.start = startValue.longValue();
			this.end = endValue.longValue();
		} else {
			Float startValue = new Float(task.getFps() * (taskItem.getEndTime() - 0.5f));
			Float endValue = new Float(task.getFps() * taskItem.getEndTime());
			this.start = startValue.longValue();
			this.end = endValue.longValue();
		}
	}

	public boolean in(Long startIndex, Long endIndex) {
		if (this.isStart) {
			return startIndex <= this.getEnd();
		}
		return endIndex >= this.getStart();
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	@Override
	public Element toXml(Document doc) {
		String eleName = this.isStart ? "startRange" : "endRange";
		Element element = doc.createElement(eleName);
		element.appendChild(this.createElement(doc, "from", String.valueOf(this.getStart())));
		element.appendChild(this.createElement(doc, "to", String.valueOf(this.getEnd())));
		return element;
	}
}

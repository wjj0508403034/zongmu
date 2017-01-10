package com.zongmu.service.report.bsd;

import java.util.HashMap;
import java.util.Map;

public class RowData {

	private String text;

	private Map<String, Object> data = new HashMap<>();

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}

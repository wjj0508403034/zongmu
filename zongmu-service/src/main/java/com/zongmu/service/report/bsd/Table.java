package com.zongmu.service.report.bsd;

import java.util.ArrayList;
import java.util.List;

public class Table {

	private String name;
	private List<Header> headers = new ArrayList<>();
	private List<RowData> rows = new ArrayList<>();

	public List<RowData> getRows() {
		return rows;
	}

	public void setRows(List<RowData> rows) {
		this.rows = rows;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	public void addRowData(RowData rowData) {
		this.rows.add(rowData);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

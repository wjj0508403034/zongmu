package com.zongmu.service.report.bsd;

import java.util.ArrayList;
import java.util.List;

public class Report {
	private Table videoTable = new Table();
	private Table pictureTable = new Table();

	private List<Table> tables = new ArrayList<>();

	public Table getVideoTable() {
		return videoTable;
	}

	public void setVideoTable(Table videoTable) {
		this.videoTable = videoTable;
	}

	public Table getPictureTable() {
		return pictureTable;
	}

	public void setPictureTable(Table pictureTable) {
		this.pictureTable = pictureTable;
	}

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
}

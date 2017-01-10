package com.zongmu.service.dto.asset;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class AssetObject {

	private AssetType assetType;
	private List<Long> tagIds = new ArrayList<>();
	private String memo;
	private DateTime recordTime;
	private Float recordLength;

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public List<Long> getTagIds() {
		return tagIds;
	}

	public void setTagIds(List<Long> tagIds) {
		this.tagIds = tagIds;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public DateTime getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(DateTime recordTime) {
		this.recordTime = recordTime;
	}

	public Float getRecordLength() {
		return recordLength;
	}

	public void setRecordLength(Float recordLength) {
		this.recordLength = recordLength;
	}

}

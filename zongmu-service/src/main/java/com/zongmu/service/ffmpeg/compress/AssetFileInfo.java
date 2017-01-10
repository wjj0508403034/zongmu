package com.zongmu.service.ffmpeg.compress;

import com.zongmu.service.video.VideoInfo;

public class AssetFileInfo {

	private String assetNo;
	private String assetFileNo;
	private String fileName;
	private VideoInfo videoInfo;

	public String getAssetFileNo() {
		return assetFileNo;
	}

	public void setAssetFileNo(String assetFileNo) {
		this.assetFileNo = assetFileNo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getAssetNo() {
		return assetNo;
	}

	public void setAssetNo(String assetNo) {
		this.assetNo = assetNo;
	}

	public VideoInfo getVideoInfo() {
		return videoInfo;
	}

	public void setVideoInfo(VideoInfo videoInfo) {
		this.videoInfo = videoInfo;
	}

}

package com.zongmu.service.dto.asset;

import com.zongmu.service.video.VideoInfo;

public class FileData {

	private String fileName;
	
	private Long fileSize;
	
	private VideoInfo videoInfo;

	public String getFileName() {
		return fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public VideoInfo getVideoInfo() {
		return videoInfo;
	}

	public void setVideoInfo(VideoInfo videoInfo) {
		this.videoInfo = videoInfo;
	}
	
	
}

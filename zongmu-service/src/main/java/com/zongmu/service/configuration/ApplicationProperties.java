package com.zongmu.service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationProperties {

	@Value("${zongmu.upload.path}")
	private String uploadFolder;

	@Value("${zongmu.landscape.internal}")
	private boolean internal;

	@Value("${zongmu.ffmpeg.shell}")
	private String ffmpegShell;

	@Value("${zongmu.ffprobe.shell}")
	private String ffprobeShell;
	
	@Value("${zongmu.service.host}")
	private String host;
	

	public String getUploadFolder() {
		return uploadFolder;
	}

	public void setUploadFolder(String uploadFolder) {
		this.uploadFolder = uploadFolder;
	}

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	public String getFfmpegShell() {
		return ffmpegShell;
	}

	public void setFfmpegShell(String ffmpegShell) {
		this.ffmpegShell = ffmpegShell;
	}

	public String getFfprobeShell() {
		return ffprobeShell;
	}

	public void setFfprobeShell(String ffprobeShell) {
		this.ffprobeShell = ffprobeShell;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}

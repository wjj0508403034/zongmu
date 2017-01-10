package com.zongmu.service.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class FfmpegServiceProperties {

	@Value("${ffmpeg.service.fps}")
	private int fps;

	@Value("${ffmpeg.service.host}")
	private String host;

	@Value("${ffmpeg.service.token}")
	private String token;

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}

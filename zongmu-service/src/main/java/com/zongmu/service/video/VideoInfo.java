package com.zongmu.service.video;

public class VideoInfo {
	private float height;
	private float width;
	private int fps;
	private float duration;
	private long framesCount;

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public long getFramesCount() {
		return framesCount;
	}

	public void setFramesCount(long framesCount) {
		this.framesCount = framesCount;
	}

}

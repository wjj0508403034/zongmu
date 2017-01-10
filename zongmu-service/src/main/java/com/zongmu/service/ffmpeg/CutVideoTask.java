package com.zongmu.service.ffmpeg;

import org.springframework.context.ApplicationContext;

import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.internal.service.FfmpegService;
import com.zongmu.service.runnable.BaseTask;

public class CutVideoTask extends BaseTask {

	private FfmpegService ffmpegService;
	private Task task;
	private AssetFile assetFile;
	private TaskItem taskItem;

	public CutVideoTask(ApplicationContext applicationContext, Task task, AssetFile assetFile, TaskItem taskItem) {
		super(applicationContext);
		this.ffmpegService = applicationContext.getBean(FfmpegService.class);
		this.task = task;
		this.assetFile = assetFile;
		this.taskItem = taskItem;
	}

	@Override
	public void run() {
		this.ffmpegService.cut(task, assetFile, taskItem);
	}

}

package com.zongmu.service.runnable;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.zongmu.service.dto.task.TaskStatus;
import com.zongmu.service.dto.task.TaskType;
import com.zongmu.service.entity.Task;
import com.zongmu.service.ffmpeg.cut.TaskInfo;
import com.zongmu.service.internal.service.TaskService;

public class VideoCutTask extends SendNotificationTask {

	private static Logger logger = Logger.getLogger(VideoCutTask.class);
	private Task task;
	private TaskService taskService;

	public VideoCutTask(ApplicationContext applicationContext, Task task) {
		super(applicationContext);
		this.task = task;
		this.taskService = applicationContext.getBean(TaskService.class);
	}

	@Override
	public void run() {
		logger.info("Start to runing video cut task ...");
		TaskInfo taskInfo = new TaskInfo();
		taskInfo.setTaskNo(this.task.getTaskNo());
		taskInfo.setAssetNo(this.task.getAssetNo());
		taskInfo.setAssetInfo(this.getAssetInfo(this.task.getAsset()));
		taskInfo.setTimeInterval(this.task.getTimeInterval());
		HttpResult result = null;
		if (this.task.getTaskType() == TaskType.VIDEO) {
			result = this.httpClient.post(this.getRequestUrl("video/cut"), taskInfo);
		} else {
			result = this.httpClient.post(this.getRequestUrl("video/extract"), taskInfo);
		}

		if (result.getStatusCode() == 200) {
			task.setTaskStatus(TaskStatus.CUTTING);
		} else {
			task.setTaskStatus(TaskStatus.CUTTINGFAILURE);
		}

		this.taskService.updateTask(task);

		logger.info("Runing video cut task finished.");
	}

}

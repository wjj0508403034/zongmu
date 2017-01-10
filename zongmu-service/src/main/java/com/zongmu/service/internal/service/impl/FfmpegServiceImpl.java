package com.zongmu.service.internal.service.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.ffmpeg.CutVideoParam;
import com.zongmu.service.internal.service.FfmpegService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.properties.FfmpegServiceProperties;
import com.zongmu.service.runnable.HttpResult;
import com.zongmu.service.util.HttpClientWrapper;
import com.zongmu.service.video.VideoInfo;

@Service
public class FfmpegServiceImpl implements FfmpegService {

	private static Logger logger = Logger.getLogger(FfmpegServiceImpl.class);

	@Autowired
	private HttpClientWrapper httpClient;

	@Autowired
	private FfmpegServiceProperties ffmpegServiceProperties;

	@Autowired
	private TaskService taskService;

	@Override
	public VideoInfo getVideoInfo(AssetFile assetFile) {
		String url = "video/info?assetNo=" + assetFile.getAssetNo() + "&fileName=" + assetFile.getFileName();
		HttpResult result = this.httpClient.get(this.getRequestUrl(url));
		if (result.getStatusCode() == 200) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				return objectMapper.readValue(result.getContent().getBytes(), VideoInfo.class);
			} catch (Exception e) {
				logger.error("Cast String to object failed");
				logger.error(ExceptionUtils.getStackTrace(e));
			}
		}

		return null;
	}

	@Override
	public void cut(Task task, AssetFile assetFile, TaskItem taskItem) {

		CutVideoParam param = new CutVideoParam();
		param.setAssetNo(task.getAssetNo());
		param.setTaskNo(task.getTaskNo());
		param.setTaskItemNo(taskItem.getTaskItemNo());
		param.setFileName(assetFile.getFileName());
		param.setOrderNo(taskItem.getOrderNo());
		param.setTimeLength(task.getTimeInterval());
		param.setAssetFileNo(assetFile.getAssetFileNo());
		param.setTaskType(task.getTaskType());

		HttpResult result = this.httpClient.post(this.getRequestUrl("video/cutVideo"), param);
		if (result.getStatusCode() != 200) {
			this.taskService.cutFailure(taskItem);
		}
	}

	private final String getRequestUrl(String path) {
		return this.ffmpegServiceProperties.getHost() + path;
	}

}

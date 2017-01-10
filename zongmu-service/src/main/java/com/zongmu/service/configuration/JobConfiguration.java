package com.zongmu.service.configuration;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.AssetService;
import com.zongmu.service.internal.service.FtpService;
import com.zongmu.service.internal.service.TaskRecordService;

@Component
public class JobConfiguration {

	private static Logger logger = Logger.getLogger(JobConfiguration.class);

	@Autowired
	private TaskRecordService taskRecordService;

	@Autowired
	private AssetService assetService;

	@Autowired
	private FtpService ftpService;

	@Autowired
	private ApplicationProperties applicationProperties;

	/**
	 * cron表达式：* * * * * *（共6位，使用空格隔开，具体如下） cron表达式：*(秒0-59) *(分钟0-59) *(小时0-23)
	 * *(日期1-31) *(月份1-12或是JAN-DEC) *(星期1-7或是SUN-SAT)
	 */
	@Scheduled(cron = "0 0 12 * * ? ")
	public void cancelTimeoutTasksCornJob() {
		logger.info("Start cancelTimeoutTasksCornJob ...");
		this.taskRecordService.cancelTimeoutTasks();
		logger.info("Finished cancelTimeoutTasksCornJob .");
	}

	/*
	 * 重新上传视频到FTP服务器,每5分钟触发一次
	 */
	@Scheduled(cron = "0 0/5 *  * * ? ")
	public void retryFtpUploadCornJob() {
		if (this.applicationProperties.isInternal()) {
			this.assetService.retryFtpUpload();
		}
	}

	@Scheduled(cron = "0/5 * *  * * ? ")
	public void uploadVideoFileJob() {
		if (this.applicationProperties.isInternal()) {
			for (AssetFile assetFile : this.assetService.getPendingUploadVideoFiles(2)) {
				try {
					this.ftpService.upload(assetFile);
				} catch (BusinessException e) {
					logger.error("Upload failed");
					logger.error(ExceptionUtils.getStackTrace(e));
				}
			}
		}
	}
}

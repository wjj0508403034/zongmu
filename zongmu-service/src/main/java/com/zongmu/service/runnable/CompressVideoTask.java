package com.zongmu.service.runnable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.zongmu.service.dto.asset.AssetFileStatus;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.ffmpeg.compress.AssetFileInfo;
import com.zongmu.service.ffmpeg.compress.AssetInfo;
import com.zongmu.service.internal.service.AssetService;

public class CompressVideoTask extends SendNotificationTask {

	private static Logger logger = Logger.getLogger(CompressVideoTask.class);
	private Asset asset;
	private AssetService assetService;

	public CompressVideoTask(ApplicationContext applicationContext, Asset asset) {
		super(applicationContext);
		this.assetService = applicationContext.getBean(AssetService.class);
		this.asset = asset;
	}

	@Override
	public void run() {
		logger.info("Start to runing compress video task ...");
		HttpResult result = this.httpClient.post(this.getRequestUrl("video/compress"), this.getAssetInfo(asset));
		if (result.getStatusCode() == 200) {
			try {
				this.assetService.updateStatus(asset, AssetFileStatus.COMPRESSING);
			} catch (Exception e) {
				logger.error("Update asset status failed.");
				logger.error(ExceptionUtils.getStackTrace(e));
			}
		}

		logger.info("Runing compress video task finished.");
	}

}

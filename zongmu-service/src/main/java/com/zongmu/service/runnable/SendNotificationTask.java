package com.zongmu.service.runnable;

import org.springframework.context.ApplicationContext;

import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.ffmpeg.compress.AssetFileInfo;
import com.zongmu.service.ffmpeg.compress.AssetInfo;
import com.zongmu.service.properties.FfmpegServiceProperties;
import com.zongmu.service.util.HttpClientWrapper;

public abstract class SendNotificationTask extends BaseTask {

	protected HttpClientWrapper httpClient;
	private FfmpegServiceProperties ffmpegServiceProperties;

	public SendNotificationTask(ApplicationContext applicationContext) {
		super(applicationContext);
		this.httpClient = applicationContext.getBean(HttpClientWrapper.class);
		this.ffmpegServiceProperties = applicationContext.getBean(FfmpegServiceProperties.class);
	}

	public final String getRequestUrl(String path) {
		return this.ffmpegServiceProperties.getHost() + path;
	}

	protected AssetInfo getAssetInfo(Asset asset) {
		AssetInfo assetInfo = new AssetInfo();
		assetInfo.setAssetNo(asset.getAssetNo());
		for (AssetFile assetFile : asset.getAssetFiles()) {
			AssetFileInfo assetFileInfo = new AssetFileInfo();
			assetFileInfo.setAssetFileNo(assetFile.getAssetFileNo());
			assetFileInfo.setFileName(assetFile.getFileName());
			assetFileInfo.setAssetNo(asset.getAssetNo());
			assetInfo.getAssetFileInfos().add(assetFileInfo);
		}

		return assetInfo;
	}

}

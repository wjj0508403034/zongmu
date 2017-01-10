package com.zongmu.service.runnable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.internal.service.FtpService;

public class UploadFileToFtp extends BaseTask {

	private static Logger logger = Logger.getLogger(UploadFileToFtp.class);

	private FtpService ftpService;
	private AssetFile assetFile;

	public UploadFileToFtp(ApplicationContext applicationContext, AssetFile assetFile) {
		super(applicationContext);
		this.ftpService = applicationContext.getBean(FtpService.class);
		this.assetFile = assetFile;
	}

	@Override
	public void run() {
		logger.info("Upload task is running ...");
		logger.info("File name: " + this.assetFile.getFileName());
		try {
			this.ftpService.uploadPic(assetFile);
		} catch (Exception ex) {
			logger.error("Upload task failed. fileName: " + this.assetFile.getFileName());
			logger.error(ExceptionUtils.getStackTrace(ex));
		}

		logger.info("Upload task is finished.");
	}

}

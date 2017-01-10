package com.zongmu.service.util;

import java.io.File;
import java.util.Random;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;

@Service
public class CommonService {
	private static Logger logger = Logger.getLogger(CommonService.class);
	private static int MAX = 9999;
	private static int MIN = 1000;

	public void createFolder(String path) throws BusinessException {
		File dir = new File(path);
		if (!dir.exists()) {
			try {
				dir.mkdir();
			} catch (SecurityException ex) {
				logger.error("Create file failed", ex);
				throw new BusinessException(ErrorCode.CREATE_FOLDER_FAILED);
			}
		}
	}

	private Random random = new Random();

	public String generateNo() {
		return DateTime.now().toString("yyyyMMddHHmmssSSS") + randomNum();
	}

	private int randomNum() {
		return this.random.nextInt(MAX) % (MAX - MIN + 1) + MIN;
	}
}

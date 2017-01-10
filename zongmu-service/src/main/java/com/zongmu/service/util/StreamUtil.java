package com.zongmu.service.util;

import java.io.Closeable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

public class StreamUtil {
	private static Logger logger = Logger.getLogger(StreamUtil.class);

	public static void safeClose(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (Exception ex) {
				logger.error("Close stream failed.", ex);
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		}
	}
}

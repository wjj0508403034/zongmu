package com.zongmu.service.ftp;

import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.log4j.Logger;

public class FtpCopyStreamListener extends CopyStreamAdapter {

	private static Logger logger = Logger.getLogger(FtpCopyStreamListener.class);

	private long totalBytes;

	public FtpCopyStreamListener(long totalBytes) {
		this.totalBytes = totalBytes;
	}

	@Override
	public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
		logger.debug("total bytes: " + totalBytes);
		logger.debug("transferred bytes: " + totalBytesTransferred);
		if (this.totalBytes != 0) {
			float precent = (totalBytesTransferred * 1.0f / totalBytes) * 100;
			logger.debug("Upload precent:" + precent + "%");
		}
	}
}

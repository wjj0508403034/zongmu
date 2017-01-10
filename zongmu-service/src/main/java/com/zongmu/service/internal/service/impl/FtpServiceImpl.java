package com.zongmu.service.internal.service.impl;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zongmu.service.dto.asset.AssetFileStatus;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.Task;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.ftp.FtpCopyStreamListener;
import com.zongmu.service.internal.service.AssetService;
import com.zongmu.service.internal.service.FtpService;
import com.zongmu.service.properties.FtpProperties;
import com.zongmu.service.properties.ProxyProperties;
import com.zongmu.service.util.FileService;

@Service
public class FtpServiceImpl implements FtpService {

	private static Logger logger = Logger.getLogger(FtpServiceImpl.class);

	private static final int BufferSize = 10 * 1024 * 1024;

	@Autowired
	private FtpProperties ftpProperties;

	@Autowired
	private ProxyProperties proxyProperties;

	@Autowired
	private AssetService assetService;

	@Autowired
	private FileService fileService;

	@Override
	public void upload(AssetFile assetFile) {
		File file = this.fileService.getFile(assetFile);
		if (file.exists()) {
			this.assetService.setAssetFileStatus(assetFile, AssetFileStatus.FTPUPLOADING);
			boolean result = this.upload(file, assetFile.getAssetNo(), assetFile.getFileName());
			this.assetService.afterUploadFTP(assetFile, result);
		} else {
			logger.warn("Asset file " + assetFile.getAssetNo() + "/" + assetFile.getFileName() + " not exist.");
		}
	}
	
	@Override
	public void uploadPic(AssetFile assetFile) {
		File file = this.fileService.getFile(assetFile);
		if (file.exists()) {
			boolean result = this.upload(file, assetFile.getAssetNo(), assetFile.getFileName());
			this.assetService.afterUploadFTP(assetFile, result);
		} else {
			logger.warn("Asset file " + assetFile.getAssetNo() + "/" + assetFile.getFileName() + " not exist.");
		}
	}
	
	@Override
	public boolean uploadXml2(File file, String assetNo,String fileName) {
		String targetFolder = assetNo;
		boolean result = false;
		FTPClient ftpClient = null;
		if (this.proxyProperties.isEnabled()) {
			ftpClient = new FTPHTTPClient(this.proxyProperties.getHost(), this.proxyProperties.getPort(),
					this.proxyProperties.getUserName(), this.proxyProperties.getPassword());
		} else {
			ftpClient = new FTPClient();
		}

		try {
			try {
				ftpClient.setCopyStreamListener(new FtpCopyStreamListener(file.length()));
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.configure(this.initFtpConfig());

				logger.info("Start to connect to ftp server...");
				ftpClient.connect(this.ftpProperties.getFtpServiceUrl());
				this.logFtpReply(ftpClient);
				logger.info("Connect to ftp server success.");
			} catch (IOException e) {
				logger.error("Connect to ftp server failed.", e);
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new BusinessException(ErrorCode.FTP_CONNECTED_FAILED);
			}

			try {
				ftpClient.enterLocalPassiveMode();
				this.logFtpReply(ftpClient);

				logger.info("Login to ftp server ...");
				Boolean loginResult = ftpClient.login(this.ftpProperties.getUserName(),
						this.ftpProperties.getPassword());
				this.logFtpReply(ftpClient);
				if (!loginResult) {
					logger.info("Login to ftp server failed.");
					throw new BusinessException(ErrorCode.FTP_LOGIN_FAILED);
				}
			} catch (IOException e) {
				logger.info("Login to ftp server failed.", e);
				throw new BusinessException(ErrorCode.FTP_CONNECTED_FAILED);
			}
			this.createDirectory(ftpClient, "upload//" + targetFolder);
			this.writeContent(ftpClient, file, fileName);
			result = true;

			try {
				ftpClient.logout();
			} catch (IOException ex) {
				logger.error("FTP logout failed", ex);
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		} catch (BusinessException ex) {

			try {
				ftpClient.abort();
			} catch (IOException e) {
				logger.error("Ftp abort failed.", e);
				logger.error(ExceptionUtils.getStackTrace(e));
			}
			logger.error("Ftp occur business exception.", ex);
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error("Ftp disconnect failed.", e);
				logger.error(ExceptionUtils.getStackTrace(e));
			}

		}

		return result;
	}

	@Override
	public boolean uploadXml(File file, Task task) {
		String targetFolder = task.getAssetNo();
		String fileName = task.getTaskNo() + ".xml";
		boolean result = false;
		FTPClient ftpClient = null;
		if (this.proxyProperties.isEnabled()) {
			ftpClient = new FTPHTTPClient(this.proxyProperties.getHost(), this.proxyProperties.getPort(),
					this.proxyProperties.getUserName(), this.proxyProperties.getPassword());
		} else {
			ftpClient = new FTPClient();
		}

		try {
			try {
				ftpClient.setCopyStreamListener(new FtpCopyStreamListener(file.length()));
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.configure(this.initFtpConfig());

				logger.info("Start to connect to ftp server...");
				ftpClient.connect(this.ftpProperties.getFtpServiceUrl());
				this.logFtpReply(ftpClient);
				logger.info("Connect to ftp server success.");
			} catch (IOException e) {
				logger.error("Connect to ftp server failed.", e);
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new BusinessException(ErrorCode.FTP_CONNECTED_FAILED);
			}

			try {
				ftpClient.enterLocalPassiveMode();
				this.logFtpReply(ftpClient);

				logger.info("Login to ftp server ...");
				Boolean loginResult = ftpClient.login(this.ftpProperties.getUserName(),
						this.ftpProperties.getPassword());
				this.logFtpReply(ftpClient);
				if (!loginResult) {
					logger.info("Login to ftp server failed.");
					throw new BusinessException(ErrorCode.FTP_LOGIN_FAILED);
				}
			} catch (IOException e) {
				logger.info("Login to ftp server failed.", e);
				throw new BusinessException(ErrorCode.FTP_CONNECTED_FAILED);
			}
			this.createDirectory(ftpClient, "upload//" + targetFolder);
			this.writeContent(ftpClient, file, fileName);
			result = true;

			try {
				ftpClient.logout();
			} catch (IOException ex) {
				logger.error("FTP logout failed", ex);
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		} catch (BusinessException ex) {

			try {
				ftpClient.abort();
			} catch (IOException e) {
				logger.error("Ftp abort failed.", e);
				logger.error(ExceptionUtils.getStackTrace(e));
			}
			logger.error("Ftp occur business exception.", ex);
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error("Ftp disconnect failed.", e);
				logger.error(ExceptionUtils.getStackTrace(e));
			}

		}

		return result;

	}

	private boolean upload(File file, String targetFolder, String fileName) {
		boolean result = false;
		FTPClient ftpClient = null;
		if (this.proxyProperties.isEnabled()) {
			ftpClient = new FTPHTTPClient(this.proxyProperties.getHost(), this.proxyProperties.getPort(),
					this.proxyProperties.getUserName(), this.proxyProperties.getPassword());
		} else {
			ftpClient = new FTPClient();
		}

		try {
			try {
				ftpClient.setCopyStreamListener(new FtpCopyStreamListener(file.length()));
				ftpClient.setControlEncoding("GBK");
				ftpClient.configure(this.initFtpConfig());

				logger.info("Start to connect to ftp server...");
				ftpClient.connect(this.ftpProperties.getFtpServiceUrl());
				this.logFtpReply(ftpClient);
				logger.info("Connect to ftp server success.");
			} catch (IOException e) {
				logger.error("Connect to ftp server failed.", e);
				logger.error(ExceptionUtils.getStackTrace(e));
				throw new BusinessException(ErrorCode.FTP_CONNECTED_FAILED);
			}

			try {
				ftpClient.enterLocalPassiveMode();
				this.logFtpReply(ftpClient);

				logger.info("Login to ftp server ...");
				Boolean loginResult = ftpClient.login(this.ftpProperties.getUserName(),
						this.ftpProperties.getPassword());
				this.logFtpReply(ftpClient);
				if (!loginResult) {
					logger.info("Login to ftp server failed.");
					throw new BusinessException(ErrorCode.FTP_LOGIN_FAILED);
				}
			} catch (IOException e) {
				logger.info("Login to ftp server failed.", e);
				throw new BusinessException(ErrorCode.FTP_CONNECTED_FAILED);
			}
			this.createDirectory(ftpClient, "upload//" + targetFolder + "//Datalog");
			this.writeContent(ftpClient, file, fileName);
			result = true;

			try {
				ftpClient.logout();
			} catch (IOException ex) {
				logger.error("FTP logout failed", ex);
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		} catch (BusinessException ex) {

			try {
				ftpClient.abort();
			} catch (IOException e) {
				logger.error("Ftp abort failed.", e);
				logger.error(ExceptionUtils.getStackTrace(e));
			}
			logger.error("Ftp occur business exception.", ex);
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error("Ftp disconnect failed.", e);
				logger.error(ExceptionUtils.getStackTrace(e));
			}

		}

		return result;
	}

	private FTPClientConfig initFtpConfig() {
		FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
		conf.setServerLanguageCode("zh");
		conf.setServerTimeZoneId(TimeZone.getDefault().getID());
		return conf;
	}

	private void writeContent(FTPClient ftpClient, File file, String fileName) throws BusinessException {
		FileInputStream fileStream = null;
		try {
			logger.info("Start to upload file ...");
			ftpClient.setBufferSize(BufferSize);
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			fileStream = new FileInputStream(file);
			ftpClient.storeFile(fileName, fileStream);
			logger.info("Upload file success.");
		} catch (IOException e) {
			logger.info("Upload file failed.");
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new BusinessException(ErrorCode.FTP_UPLOADED_FAILED);
		} finally {
			this.safeClose(fileStream);
		}
	}

	private void safeClose(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (Exception ex) {
				logger.error("Close stream failed.", ex);
				logger.error(ExceptionUtils.getStackTrace(ex));
			}
		}
	}

	private synchronized void createDirectory(FTPClient ftpClient, String dir) throws BusinessException {
		logger.info("Start to create directory " + dir + " ...");
		String[] parts = dir.split("/");
		for (String part : parts) {
			if (!StringUtils.isEmpty(part)) {
				if (!this.isDirExist(ftpClient, part)) {
					try {
						Boolean result = ftpClient.makeDirectory(part);
						this.logFtpReply(ftpClient);
						if (!result) {
							logger.error("Create directory '" + part + "' failed.");
							throw new BusinessException(ErrorCode.FTP_CREATE_FOLDER_FAILED);
						}

						logger.info("Create directory '" + part + "' success.");

						logger.info("Change work directory to '" + part + "'");
						ftpClient.changeWorkingDirectory(part);
						this.logFtpReply(ftpClient);
					} catch (IOException e) {
						logger.error(ExceptionUtils.getStackTrace(e));
						throw new BusinessException(ErrorCode.FTP_CREATE_FOLDER_FAILED);
					}
				} else {
					try {
						logger.info("Change work directory to '" + part + "'");
						ftpClient.changeWorkingDirectory(part);
						this.logFtpReply(ftpClient);
					} catch (IOException e) {
						logger.error(ExceptionUtils.getStackTrace(e));
						throw new BusinessException(ErrorCode.FTP_CREATE_FOLDER_FAILED);
					}
				}
			}
		}

		logger.info("Create directory '" + dir + "' success.");
	}

	private boolean isDirExist(FTPClient ftpClient, String dirName) throws BusinessException {
		try {
			logger.info("Check directory '" + dirName + "' exist...");
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			this.logFtpReply(ftpClient);

			FTPFile[] files = ftpClient.listDirectories();
			this.logFtpReply(ftpClient);
			for (FTPFile file : files) {
				if (file.getName().endsWith(dirName)) {
					logger.info("Directory '" + dirName + "' exist.");
					return true;
				}
			}
		} catch (IOException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new BusinessException(ErrorCode.FTP_LIST_FOLDER_FAILED);
		}

		logger.info("Directory '" + dirName + "' not exist.");
		return false;
	}

	private void logFtpReply(FTPClient ftpClient) {
		logger.info(ftpClient.getReplyString());
	}



}

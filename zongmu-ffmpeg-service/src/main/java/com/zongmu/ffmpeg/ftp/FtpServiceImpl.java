package com.zongmu.ffmpeg.ftp;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.TimeZone;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.zongmu.ffmpeg.entities.FileUploadStatus;
import com.zongmu.ffmpeg.entities.TaskItemFile;
import com.zongmu.ffmpeg.exception.BusinessException;
import com.zongmu.ffmpeg.exception.ErrorCode;
import com.zongmu.ffmpeg.properties.AliYunProperties;
import com.zongmu.ffmpeg.services.TaskService;

@Service
public class FtpServiceImpl implements FtpService{

    private static Logger logger = Logger.getLogger(FtpServiceImpl.class);
    
    private static final int BufferSize = 5 * 1024 * 1024;
    
    @Autowired
    private AliYunProperties aliyunProps;
    
    @Autowired
    private TaskService taskService;

    @Override
    public void upload(File file,TaskItemFile taskItemFile) {
        FTPClient ftpClient = new FTPClient();

        try {
            try {
                ftpClient.setCopyStreamListener(new FtpCopyStreamListener(file.length()));
                ftpClient.setControlEncoding("GBK");
                ftpClient.configure(this.initFtpConfig());

                logger.info("Start to connect to ftp server...");
                ftpClient.connect(this.aliyunProps.getHost());
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
                Boolean loginResult = ftpClient.login(this.aliyunProps.getUserName(),
                        this.aliyunProps.getPassword());
                this.logFtpReply(ftpClient);
                if (!loginResult) {
                    logger.info("Login to ftp server failed.");
                    throw new BusinessException(ErrorCode.FTP_LOGIN_FAILED);
                }
            } catch (IOException e) {
                logger.info("Login to ftp server failed.", e);
                throw new BusinessException(ErrorCode.FTP_CONNECTED_FAILED);
            }
            this.createDirectory(ftpClient, "upload//" + taskItemFile.getTask().getAssetNo() + "//Datalog//compress//" + taskItemFile.getTask().getTaskNo());
            this.writeContent(ftpClient, file, file.getName());
            taskItemFile.setUploadStatus(FileUploadStatus.UploadingSuccess);
            this.taskService.updateTaskItemFile(taskItemFile);
            try {
                ftpClient.logout();
            } catch (IOException ex) {
                logger.error("FTP logout failed", ex);
                logger.error(ExceptionUtils.getStackTrace(ex));
            }
        } catch (BusinessException ex) {
            taskItemFile.setUploadStatus(FileUploadStatus.UploadingFailed);
            this.taskService.updateTaskItemFile(taskItemFile);
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

package com.zongmu.ffmpeg.configuration;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zongmu.ffmpeg.entities.AssetFile;
import com.zongmu.ffmpeg.entities.FileUploadStatus;
import com.zongmu.ffmpeg.entities.TaskItem;
import com.zongmu.ffmpeg.entities.TaskItemFile;
import com.zongmu.ffmpeg.ftp.FtpService;
import com.zongmu.ffmpeg.properties.FfmpegProperties;
import com.zongmu.ffmpeg.services.NewVideoService;
import com.zongmu.ffmpeg.services.TaskService;

@EnableAsync
@EnableScheduling
@Component
public class JobConfiguration {

    private static Logger logger = Logger.getLogger(JobConfiguration.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private NewVideoService newVideoService;

    @Autowired
    private FtpService ftpService;

    @Autowired
    private FfmpegProperties ffmpeg;

    @Scheduled(cron = "0/5 * *  * * ? ")
    public void cutVideoJob() {
        List<TaskItem> taskItems = this.taskService.getPendingTaskItems(10);
        for (TaskItem taskItem : taskItems) {
            logger.info("Video job start...");
            this.newVideoService.run(taskItem);
            logger.info("Video job end.");
        }
    }

    @Scheduled(cron = "0/3 * *  * * ? ")
    public void moveDeleteAssetsJob() {
        this.taskService.moveDeleteAssetFiles(1);
    }

    @Scheduled(cron = "0/10 * *  * * ? ")
    public void retryCutVideoJob() {
        List<TaskItem> taskItems = this.taskService.getFailedTaskItems(20);
        for (TaskItem taskItem : taskItems) {
            logger.info("Retry video job start...");
            this.newVideoService.run(taskItem);
            logger.info("Retry video job end.");
        }
    }

    @Async
    @Scheduled(cron = "0/3 * *  * * ? ")
    public void compressVideoJob1() {
        List<AssetFile> assetFiles = this.taskService.getPendingAssetFiles(1);
        for (AssetFile assetFile : assetFiles) {
            logger.info("compress job 1 start...");
            this.newVideoService.compress(assetFile);
            logger.info("compress job 1 end.");
        }
    }

    @Async
    @Scheduled(cron = "0/5 * *  * * ? ")
    public void compressVideoJob2() {
        List<AssetFile> assetFiles = this.taskService.getPendingAssetFiles(1);
        for (AssetFile assetFile : assetFiles) {
            logger.info("compress job 2 start...");
            this.newVideoService.compress(assetFile);
            logger.info("compress job 2 end.");
        }
    }

    @Async
    @Scheduled(cron = "0/7 * *  * * ? ")
    public void compressVideoJob3() {
        List<AssetFile> assetFiles = this.taskService.getPendingAssetFiles(1);
        for (AssetFile assetFile : assetFiles) {
            logger.info("compress job 3 start...");
            this.newVideoService.compress(assetFile);
            logger.info("compress job 3 end.");
        }
    }

    @Async
    @Scheduled(cron = "0/8 * *  * * ? ")
    public void compressVideoJob4() {
        List<AssetFile> assetFiles = this.taskService.getPendingAssetFiles(1);
        for (AssetFile assetFile : assetFiles) {
            logger.info("compress job 4 start...");
            this.newVideoService.compress(assetFile);
            logger.info("compress job 4 end.");
        }
    }

    @Async
    @Scheduled(cron = "0/4 * *  * * ? ")
    public void compressFailedVideoJob1() {
        List<AssetFile> assetFiles = this.taskService.getCompressFailedAssetFile(1);
        for (AssetFile assetFile : assetFiles) {
            logger.info("compress failed job 1 start...");
            this.newVideoService.compressFailed(assetFile);
            logger.info("compress failed job 1 end.");
        }
    }

    //@Scheduled(cron = "0/1 * *  * * ? ")
    public void uploadVideoToAliyun() {
        List<TaskItemFile> taskItemFiles = this.taskService.getPendingUploadFiles(1);
        for (TaskItemFile taskItemFile : taskItemFiles) {
            if(taskItemFile.getUploadStatus() == FileUploadStatus.None){
                logger.info("upload video to aliyun start ..." + taskItemFile.getTaskItemFileNo());
                String filePath = this.ffmpeg.getWorkspace() + taskItemFile.getPath();
                File file = new File(filePath);
                if (file.exists()) {
                    taskItemFile.setUploadStatus(FileUploadStatus.Uploading);
                    this.taskService.updateTaskItemFile(taskItemFile);
                    this.ftpService.upload(file, taskItemFile);
                } else {
                    taskItemFile.setUploadStatus(FileUploadStatus.FileNoExist);
                    this.taskService.updateTaskItemFile(taskItemFile);
                }

                logger.info("upload video to aliyun end."); 
            }
        }
    }
}

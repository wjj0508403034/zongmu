package com.zongmu.ffmpeg.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.zongmu.ffmpeg.compress.dto.AssetFileInfo;
import com.zongmu.ffmpeg.cut.dto.TaskInfo;
import com.zongmu.ffmpeg.cut.dto.TaskItemFileInfo;
import com.zongmu.ffmpeg.cut.dto.TaskItemInfo;

public class ExtractTask extends VideoTask {

    private static Logger logger = Logger.getLogger(ExtractTask.class);
    private TaskInfo taskInfo;

    public ExtractTask(ApplicationContext applicationContext, TaskInfo taskInfo) {
        super(applicationContext);
        this.taskInfo = taskInfo;
    }

    @Override
    protected String getWorkspace() {
        return this.ffmpeg.getWorkspace() + this.taskInfo.getAssetNo() + "\\Datalog\\compress\\";
    }

    @Override
    public void run() {
        logger.info("Cut task is running...");

        String targetDirectory = this.getWorkspace() + this.taskInfo.getTaskNo();
        if (this.safeCreateFolder(targetDirectory)) {
            for (AssetFileInfo assetFileInfo : this.taskInfo.getAssetInfo().getAssetFileInfos()) {
                assetFileInfo.setVideoInfo(this.getVideoInfo(assetFileInfo.getFileName()));
            }

            for (AssetFileInfo assetFileInfo : this.taskInfo.getAssetInfo().getAssetFileInfos()) {
                if (assetFileInfo.getVideoInfo() != null) {
                    int counter = 0;
                    float time = 0;
                    while (time < assetFileInfo.getVideoInfo().getDuration()) {
                        TaskItemInfo taskItemInfo = null;

                        if (this.taskInfo.getTaskItemInfos().size() <= counter) {
                            taskItemInfo = new TaskItemInfo();
                            taskItemInfo.setOrder(counter);
                            this.taskInfo.getTaskItemInfos().add(taskItemInfo);
                        } else {
                            taskItemInfo = this.taskInfo.getTaskItemInfos().get(counter);
                        }

                        String targetFile = String.format("%s_%d.jpg", assetFileInfo.getFileName(), counter);
                        TaskItemFileInfo taskItemFileInfo = this.cut(assetFileInfo.getFileName(), targetFile, time);
                        taskItemInfo.getTaskItemFileInfos().add(taskItemFileInfo);
                        counter++;
                        time += this.taskInfo.getTimeInterval();
                    }
                }
            }

            if (this.taskInfo.getTaskItemInfos().size() > 0) {
                this.zongmuService.cut(this.taskInfo);
            }
        }

        logger.info("Cut task is finished.");

    }

    private TaskItemFileInfo cut(String originalFile, String targetFile, float startTime) {
        /*
         * ffmpeg.exe -i right.avi -y -f image2 -ss 15 -vframes 1 xxx.jpg
         */

        List<String> arguments = new ArrayList<String>();
        arguments.add(ffmpeg.getShell());
        arguments.add("-i");
        arguments.add(originalFile);
        arguments.add("-y");
        arguments.add("-f");
        arguments.add("image2");
        arguments.add("-ss");
        arguments.add(Float.toString(startTime));
        arguments.add("-vframes");
        arguments.add("1");
        arguments.add(this.taskInfo.getTaskNo() + "\\" + targetFile);
        CommandResult result = this.execCommand(arguments);
        TaskItemFileInfo taskItemFileInfo = new TaskItemFileInfo();
        taskItemFileInfo.setFileName(targetFile);
        taskItemFileInfo.setSuccess(result.isSuccess());

        return taskItemFileInfo;
    }

}

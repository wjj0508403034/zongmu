package com.zongmu.ffmpeg.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.zongmu.ffmpeg.compress.dto.AssetFileInfo;

public class CompressTask extends VideoTask {

    private static Logger logger = Logger.getLogger(CompressTask.class);

    private AssetFileInfo assetFileInfo;

    public CompressTask(ApplicationContext applicationContext, AssetFileInfo assetFileInfo) {
        super(applicationContext);
        this.assetFileInfo = assetFileInfo;
    }

    @Override
    protected String getWorkspace() {
        return this.ffmpeg.getWorkspace() + this.assetFileInfo.getAssetNo() + "\\Datalog\\";
    }

    @Override
    public void run() {
        logger.info("Compress task is running...");
        String fps = "24"; // Default FPS

        String targetDirectory = this.getWorkspace() + "compress";
        if (this.safeCreateFolder(targetDirectory)) {
            String sourceFile = this.assetFileInfo.getFileName();
            String targetFile = "compress\\" + this.assetFileInfo.getFileName();

            /*
             * ffmpeg -i input.avi -y -r 24 output.avi
             */
            List<String> arguments = new ArrayList<String>();
            arguments.add(ffmpeg.getShell());
            arguments.add("-i");
            arguments.add(sourceFile);
            arguments.add("-y");
            arguments.add("-r");
            arguments.add(fps);
            arguments.add(targetFile);
            CommandResult result = this.execCommand(arguments);
            if (result.isSuccess()) {
                logger.info("Compress video success.");
                this.zongmuService.compressSuccess(this.assetFileInfo);
            } else {
                logger.info("Compress video failed.");
                this.zongmuService.compressFailed(this.assetFileInfo);
            }
        }

        logger.info("Compress task is finished...");
    }

}

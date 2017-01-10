package com.zongmu.ffmpeg.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.zongmu.ffmpeg.cut.dto.TaskType;
import com.zongmu.ffmpeg.dto.CutResult;
import com.zongmu.ffmpeg.dto.CutVideoParam;

public class CutVideoTask extends VideoTask {

    private static Logger logger = Logger.getLogger(CutVideoTask.class);

    private CutVideoParam cutVideoParam;

    public CutVideoTask(ApplicationContext applicationContext, CutVideoParam cutVideoParam) {
        super(applicationContext);
        this.cutVideoParam = cutVideoParam;
    }

    @Override
    protected String getWorkspace() {
        return this.ffmpeg.getWorkspace() + this.cutVideoParam.getAssetNo() + "\\Datalog\\compress\\";
    }

    @Override
    public void run() {
        boolean result = false;
        String targetFile = null;
        logger.info("Cut video task is running...");
        String targetDirectory = this.getWorkspace() + this.cutVideoParam.getTaskNo();
        float startTime = this.cutVideoParam.getOrderNo() * this.cutVideoParam.getTimeLength();
        if (this.safeCreateFolder(targetDirectory)) {
            if (cutVideoParam.getTaskType() == TaskType.VIDEO) {
                targetFile = String.format("%s_%d.mp4", cutVideoParam.getFileName(), this.cutVideoParam.getOrderNo());
                result = this.cutVideo(targetFile, startTime);
            } else {
                targetFile = String.format("%s_%d.jpg", cutVideoParam.getFileName(), this.cutVideoParam.getOrderNo());
                result = this.cutImage(targetFile, startTime);
            }

        }

        CutResult cutResult = new CutResult();
        cutResult.setAssetNo(this.cutVideoParam.getAssetNo());
        cutResult.setAssetFileNo(this.cutVideoParam.getAssetFileNo());
        cutResult.setTaskItemNo(this.cutVideoParam.getTaskItemNo());
        cutResult.setTaskNo(this.cutVideoParam.getTaskNo());
        cutResult.setResult(result);
        cutResult.setFileName(targetFile);

        this.zongmuService.sendCutVideoResult(this.cutVideoParam.getTaskItemNo(), cutResult);
        logger.info("Cut task is finished.");
    }

    private boolean cutImage(String targetFile, float startTime) {
        /*
         * ffmpeg.exe -i right.avi -y -f image2 -ss 15 -vframes 1 xxx.jpg
         */

        List<String> arguments = new ArrayList<String>();
        arguments.add(ffmpeg.getShell());
        arguments.add("-i");
        arguments.add(cutVideoParam.getFileName());
        arguments.add("-y");
        arguments.add("-f");
        arguments.add("image2");
        arguments.add("-ss");
        arguments.add(Float.toString(startTime));
        arguments.add("-vframes");
        arguments.add("1");
        arguments.add(cutVideoParam.getTaskNo() + "\\" + targetFile);
        CommandResult result = this.execCommand(arguments);

        return result.isSuccess();
    }

    private boolean cutVideo(String targetFile, float startTime) {

        /*
         * ffmpeg.exe -ss 30 -t 30 -i right.avi -y -r 24 output.mp4
         */

        List<String> arguments = new ArrayList<String>();
        arguments.add(ffmpeg.getShell());
        arguments.add("-ss");
        arguments.add(Float.toString(startTime));
        arguments.add("-t");
        arguments.add(Integer.toString(cutVideoParam.getTimeLength()));
        arguments.add("-i");
        arguments.add(cutVideoParam.getFileName());
        arguments.add("-y");
        arguments.add("-r");
        arguments.add("76");
        arguments.add(cutVideoParam.getTaskNo() + "\\" + targetFile);
        CommandResult result = this.execCommand(arguments);
        return result.isSuccess();
    }

}

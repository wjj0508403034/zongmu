package com.zongmu.ffmpeg.task;

import com.zongmu.ffmpeg.entities.AssetFile;
import com.zongmu.ffmpeg.entities.TaskItem;

public class CommandResult {

    private boolean success;
    private String output;
    private TaskItem taskItem;
    private AssetFile assetFile;
    private String fileName;
    private CommandType commandType;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public TaskItem getTaskItem() {
        return taskItem;
    }

    public void setTaskItem(TaskItem taskItem) {
        this.taskItem = taskItem;
    }

    public AssetFile getAssetFile() {
        return assetFile;
    }

    public void setAssetFile(AssetFile assetFile) {
        this.assetFile = assetFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }
}

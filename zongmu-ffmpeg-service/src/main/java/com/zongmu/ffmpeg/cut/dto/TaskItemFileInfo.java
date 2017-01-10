package com.zongmu.ffmpeg.cut.dto;

public class TaskItemFileInfo {
    private String assetFileNo;
    private String fileName;
    private boolean success;

    public String getAssetFileNo() {
        return assetFileNo;
    }

    public void setAssetFileNo(String assetFileNo) {
        this.assetFileNo = assetFileNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

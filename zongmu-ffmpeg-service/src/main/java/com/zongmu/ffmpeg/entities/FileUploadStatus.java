package com.zongmu.ffmpeg.entities;

public enum FileUploadStatus {

    None,
    Uploading,
    UploadingFailed,
    UploadingSuccess,
    Deleted,
    FileNoExist,
    TaskNotExist
}

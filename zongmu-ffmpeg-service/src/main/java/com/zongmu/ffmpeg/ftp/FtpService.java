package com.zongmu.ffmpeg.ftp;

import java.io.File;

import com.zongmu.ffmpeg.entities.TaskItemFile;

public interface FtpService {

    void upload(File file,TaskItemFile taskItemFile);
}

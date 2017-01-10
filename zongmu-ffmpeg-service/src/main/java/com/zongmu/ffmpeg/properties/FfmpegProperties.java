package com.zongmu.ffmpeg.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class FfmpegProperties {

    @Value("${ffmpeg.shell}")
    private String shell;

    @Value("${ffmpeg.workspace}")
    private String workspace;

    @Value("${ffmpeg.ffprobe}")
    private String ffprobe;

    @Value("${ffmpeg.old}")
    private String old;

    public String getShell() {
        return shell;
    }

    public void setShell(String shell) {
        this.shell = shell;
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getFfprobe() {
        return ffprobe;
    }

    public void setFfprobe(String ffprobe) {
        this.ffprobe = ffprobe;
    }

    public String getOld() {
        return old;
    }

    public void setOld(String old) {
        this.old = old;
    }
}

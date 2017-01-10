package com.zongmu.ffmpeg.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.ffmpeg.compress.dto.AssetFileInfo;
import com.zongmu.ffmpeg.compress.dto.AssetInfo;
import com.zongmu.ffmpeg.cut.dto.TaskInfo;
import com.zongmu.ffmpeg.dto.CutVideoParam;
import com.zongmu.ffmpeg.dto.VideoInfo;
import com.zongmu.ffmpeg.internal.service.VideoService;

@Controller
@RequestMapping("/video")
public class VideoController {

    private static Logger logger = Logger.getLogger(VideoController.class);

    @Autowired
    private VideoService videoService;

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public VideoInfo getVideoInfo(@RequestParam(value = "assetNo") String assetNo, @RequestParam(value = "fileName") String fileName) {
        AssetFileInfo assetFileInfo = new AssetFileInfo();
        assetFileInfo.setAssetNo(assetNo);
        assetFileInfo.setFileName(fileName);
        return this.videoService.getVideoInfo(assetFileInfo);
    }

    @RequestMapping(value = "/cutVideo", method = RequestMethod.POST)
    @ResponseBody
    public void cutVideo(@RequestBody CutVideoParam cutVideoParam) {
        //this.videoService.cutVideo(cutVideoParam);
    }

    @RequestMapping(value = "/compress", method = RequestMethod.POST)
    @ResponseBody
    public void compress(@RequestBody AssetInfo assetInfo) {
        //this.videoService.compressAsset(assetInfo);
    }

    @RequestMapping(value = "/cut", method = RequestMethod.POST)
    @ResponseBody
    public void cut(@RequestBody TaskInfo taskInfo) {
        //this.videoService.cut(taskInfo);
    }

    @RequestMapping(value = "/extract", method = RequestMethod.POST)
    @ResponseBody
    public void extract(@RequestBody TaskInfo taskInfo) {
        //this.videoService.extract(taskInfo);
    }
}

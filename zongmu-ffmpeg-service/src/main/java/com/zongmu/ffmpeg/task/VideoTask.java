package com.zongmu.ffmpeg.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.zongmu.ffmpeg.dto.VideoInfo;
import com.zongmu.ffmpeg.internal.service.ZongmuService;
import com.zongmu.ffmpeg.properties.FfmpegProperties;
import com.zongmu.ffmpeg.util.StreamUtil;

public abstract class VideoTask extends BaseTask {

    private static Logger logger = Logger.getLogger(VideoTask.class);

    protected FfmpegProperties ffmpeg;
    protected ZongmuService zongmuService;

    public VideoTask(ApplicationContext applicationContext) {
        super(applicationContext);
        this.ffmpeg = applicationContext.getBean(FfmpegProperties.class);
        this.zongmuService = applicationContext.getBean(ZongmuService.class);
    }

    protected String getWorkspace() {
        return this.ffmpeg.getWorkspace();
    }

    protected VideoInfo getVideoInfo(String fileName) {

        logger.info("Start to get video (" + fileName + ") info ...");

        /*
         * ffprobe.exe -loglevel error -show_streams output.mp4 -print_format
         * xml=x=1 -noprivate
         */
        List<String> arguments = new ArrayList<String>();
        arguments.add(this.ffmpeg.getFfprobe());
        arguments.add("-loglevel");
        arguments.add("error");
        arguments.add("-show_streams");
        arguments.add(fileName);
        arguments.add("-print_format");
        arguments.add("xml=x=1");
        arguments.add("-noprivate");
        CommandResult result = this.execCommand(arguments);
        if (result.isSuccess()) {
            return this.parse(result.getOutput());
        }
        return null;
    }

    private VideoInfo parse(String xmlContent) {
        try {
            DocumentBuilderFactory xdocFactory = DocumentBuilderFactory.newInstance();
            xdocFactory.setNamespaceAware(true);
            DocumentBuilder xdoc;
            xdoc = xdocFactory.newDocumentBuilder();
            InputSource source = new InputSource();
            source.setCharacterStream(new StringReader(xmlContent));
            Document doc = xdoc.parse(source);
            NodeList nodeList = doc.getElementsByTagName("stream");
            if (nodeList.getLength() > 0) {
                Node streamNode = nodeList.item(0);
                NamedNodeMap attrsMap = streamNode.getAttributes();
                Node widthAttr = attrsMap.getNamedItem("width");
                Node heightAttr = attrsMap.getNamedItem("height");
                Node durationAttr = attrsMap.getNamedItem("duration");
                Node timeBaseAttr = attrsMap.getNamedItem("time_base");
                if (widthAttr != null && heightAttr != null && durationAttr != null && timeBaseAttr != null) {
                    VideoInfo videoInfo = new VideoInfo();
                    videoInfo.setWidth(Float.parseFloat(widthAttr.getNodeValue()));
                    videoInfo.setHeight(Float.parseFloat(heightAttr.getNodeValue()));
                    videoInfo.setDuration(Float.parseFloat(durationAttr.getNodeValue()));

                    if (timeBaseAttr.getNodeValue() != null) {
                        String[] parts = timeBaseAttr.getNodeValue().split("/");
                        if (parts.length == 2) {
                            videoInfo.setFps(Integer.parseInt(parts[1]));
                        }
                    }
                    return videoInfo;
                }
            }
        } catch (Exception ex) {
            logger.error("Parse xml content to video info failed", ex);
            logger.error(ExceptionUtils.getStackTrace(ex));
        }

        return null;
    }

    protected CommandResult execCommand(List<String> arguments) {
        StringBuilder commandText = new StringBuilder();
        String[] command = new String[arguments.size()];
        for (int index = 0; index < arguments.size(); index++) {
            command[index] = arguments.get(index);
            commandText.append(arguments.get(index) + " ");
        }
        CommandResult result = new CommandResult();
        logger.info("Command: " + commandText);
        try {
            StringBuilder sb = new StringBuilder();
            ProcessBuilder pb = new ProcessBuilder(command);
            logger.info("Current Work space: " + this.getWorkspace());
            pb.directory(new File(this.getWorkspace()));
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String line = null;
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            logger.info("Exec Result:");
            while ((line = inputReader.readLine()) != null) {
                //logger.info(line);
                sb.append(line);
            }
            result.setSuccess(p.waitFor() == 0);
            result.setOutput(sb.toString());
            logger.info("Shell running is finish. result code:  " + p.waitFor());
            StreamUtil.safeClose(p.getInputStream());
            StreamUtil.safeClose(p.getOutputStream());
            StreamUtil.safeClose(p.getErrorStream());
            p.destroy();
        } catch (Exception ex) {
            logger.error("run shell script failed.", ex);
            logger.error(ExceptionUtils.getStackTrace(ex));
            result.setSuccess(false);
        }

        return result;
    }

    protected boolean safeCreateFolder(String path) {
        File target = new File(path);
        if (!target.exists()) {
            try {
                target.mkdir();
                logger.error("Create folder success:" + path);
            } catch (Exception ex) {
                logger.error("Create folder failed.", ex);
                logger.error(ExceptionUtils.getStackTrace(ex));
                return false;
            }
        }

        return true;
    }
}

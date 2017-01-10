package com.zongmu.ffmpeg.services;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.zongmu.ffmpeg.dto.VideoInfo;
import com.zongmu.ffmpeg.entities.AssetFile;
import com.zongmu.ffmpeg.entities.AssetFileStatus;
import com.zongmu.ffmpeg.entities.TaskItem;
import com.zongmu.ffmpeg.entities.TaskType;
import com.zongmu.ffmpeg.properties.FfmpegProperties;
import com.zongmu.ffmpeg.task.CommandResult;
import com.zongmu.ffmpeg.task.CommandType;
import com.zongmu.ffmpeg.util.StreamUtil;

@Service
public class NewVideoServiceImpl implements NewVideoService {

    private static Logger logger = Logger.getLogger(NewVideoServiceImpl.class);

    @Autowired
    private FfmpegProperties ffmpeg;

    @Autowired
    private TaskService taskService;

    @Override
    public void run(TaskItem taskItem) {

        if (taskItem.getTaskType() == TaskType.VIDEO) {
            this.runCutVideoTask(taskItem);
        } else {
            this.runExtractPictureTask(taskItem);
        }
    }

    @Override
    public void compressFailed(AssetFile assetFile) {
        if (assetFile.getDuration() == 0) {
            List<String> arguments = buildVideoInfoCommandArgs(assetFile);
            CommandResult result = this.execCommand(this.getWorkspace(assetFile), arguments);
            if (result.isSuccess()) {
                VideoInfo videoInfo = this.parse(result.getOutput());
                if (videoInfo != null) {
                    assetFile.setDuration(videoInfo.getDuration());
                    assetFile.setFps(videoInfo.getFps());
                    assetFile.setHeight(videoInfo.getHeight());
                    assetFile.setWidth(videoInfo.getWidth());
                }
            } else {
                return;
            }
        }

        assetFile.setAssetFileStatus(AssetFileStatus.COMPRESSING);
        this.taskService.updateAssetFile(assetFile);
        this.compress(assetFile);
    }

    @Override
    public void compress(AssetFile assetFile) {
        logger.info("Start to compress task ...");
        String filePath = this.getWorkspace(assetFile) + assetFile.getFileName();
        logger.info("File name:" + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            this.taskService.setCompressState(assetFile);
            String targetDirectory = this.getTargetDirectory(assetFile);
            if (this.safeCreateFolder(targetDirectory)) {
                String targetFileName = "compress\\" + assetFile.getFileName();
                List<String> arguments = buildCompressVideoCommandArgs(assetFile, targetFileName);
                CommandResult result = this.execCommand(this.getWorkspace(assetFile), arguments);
                result.setAssetFile(assetFile);
                result.setCommandType(CommandType.CompressVideo);
                this.taskService.afterExecutionCommand(result);
            }
        } else {
            logger.warn("File not exist: " + filePath);
        }
        logger.info("Compress task finished.");
    }

    private void runCutVideoTask(TaskItem taskItem) {
        logger.info("Start to run cut video task ...");
        String targetDirectory = this.getTargetDirectory(taskItem);
        this.safeCreateFolder(targetDirectory);
        for (AssetFile assetFile : taskItem.getAssetFiles()) {
            String filePath = this.getWorkspace(assetFile) + assetFile.getFileName();
            logger.info("File name:" + filePath);
            File file = new File(filePath);
            if (file.exists()) {
                this.cutSingleVideoTask(assetFile, taskItem);
            } else {
                logger.warn("File not exist: " + filePath);
            }
        }

        logger.info("Run cut video task finished.");
    }

    private void runExtractPictureTask(TaskItem taskItem) {
        logger.info("Start to extract picture task ...");
        String targetDirectory = this.getTargetDirectory(taskItem);
        this.safeCreateFolder(targetDirectory);
        for (AssetFile assetFile : taskItem.getAssetFiles()) {
            String filePath = this.getWorkspace(assetFile) + assetFile.getFileName();
            logger.info("File name:" + filePath);
            File file = new File(filePath);
            if (file.exists()) {
                this.extractSinglePictureTask(assetFile, taskItem);
            } else {
                logger.warn("File not exist: " + filePath);
            }
        }
        logger.info("Start to extract picture task ...");
    }

    private void extractSinglePictureTask(AssetFile assetFile, TaskItem taskItem) {
        String targetFileName = String.format("%s_%d.jpg", assetFile.getFileName(), taskItem.getOrderNo());
        List<String> arguments = this.buildExtractPictureCommandArgs(assetFile, taskItem, targetFileName);
        CommandResult result = this.execCommand(this.getWorkspace(taskItem), arguments);
        result.setTaskItem(taskItem);
        result.setAssetFile(assetFile);
        result.setFileName(targetFileName);
        result.setCommandType(CommandType.ExtractPicture);
        this.taskService.afterExecutionCommand(result);
    }

    private void cutSingleVideoTask(AssetFile assetFile, TaskItem taskItem) {
        String targetFileName = String.format("%s_%d.mp4", assetFile.getFileName(), taskItem.getOrderNo());
        List<String> arguments = this.buildCutVideoCommandArgs(assetFile, taskItem, targetFileName);
        CommandResult result = this.execCommand(this.getWorkspace(taskItem), arguments);
        result.setTaskItem(taskItem);
        result.setAssetFile(assetFile);
        result.setFileName(targetFileName);
        result.setCommandType(CommandType.CutVideo);
        this.taskService.afterExecutionCommand(result);
    }

    private List<String> buildCompressVideoCommandArgs(AssetFile assetFile, String targetFileName) {
        List<String> arguments = new ArrayList<String>();
        arguments.add(ffmpeg.getShell());
        arguments.add("-i");
        arguments.add(assetFile.getFileName());
        arguments.add("-y");
        arguments.add("-r");
        arguments.add(String.valueOf(assetFile.getFps()));
        arguments.add(targetFileName);
        return arguments;
    }

    private List<String> buildExtractPictureCommandArgs(AssetFile assetFile, TaskItem taskItem, String targetFileName) {
        List<String> arguments = new ArrayList<String>();
        arguments.add(ffmpeg.getShell());
        arguments.add("-i");
        arguments.add(assetFile.getFileName());
        arguments.add("-y");
        arguments.add("-f");
        arguments.add("image2");
        arguments.add("-ss");
        arguments.add(Integer.toString(taskItem.getTask().getTimeInterval()));
        arguments.add("-vframes");
        arguments.add("1");
        arguments.add(taskItem.getTask().getTaskNo() + "\\" + targetFileName);
        return arguments;
    }

    private List<String> buildCutVideoCommandArgs(AssetFile assetFile, TaskItem taskItem, String targetFileName) {

        /*
         * ffmpeg.exe -ss 30 -t 30 -i right.avi -y -r 24 output.mp4
         */

        List<String> arguments = new ArrayList<String>();
        arguments.add(ffmpeg.getShell());
        arguments.add("-ss");
        arguments.add(String.valueOf(getStartTime(taskItem)));
        arguments.add("-t");
        arguments.add(String.valueOf(getVideoLength(taskItem)));
        arguments.add("-i");
        arguments.add(assetFile.getFileName());
        arguments.add("-y");
        arguments.add("-r");
        arguments.add(String.valueOf(assetFile.getFps()));
        arguments.add(taskItem.getTask().getTaskNo() + "\\" + targetFileName);
        return arguments;
    }

    private float getStartTime(TaskItem taskItem) {
        if (taskItem.getOrderNo() == 0) {
            return taskItem.getTask().getTimeInterval() * taskItem.getOrderNo();
        } else {
            return taskItem.getTask().getTimeInterval() * taskItem.getOrderNo() - 0.5f;
        }
    }
    
    private float getVideoLength(TaskItem taskItem){
        if (taskItem.getOrderNo() == 0) {
            return taskItem.getTask().getTimeInterval();
        }else{
            return taskItem.getTask().getTimeInterval() + 0.5f;
        }
    }

    private CommandResult execCommand(String workspace, List<String> arguments) {
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
            logger.info("Current Work space: " + workspace);
            pb.directory(new File(workspace));
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String line = null;
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            logger.debug("Exec Result:");
            while ((line = inputReader.readLine()) != null) {
                logger.info(line);
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

    private String getWorkspace(TaskItem taskItem) {
        String dir = this.ffmpeg.getWorkspace() + taskItem.getAssetNo() + "\\Datalog\\compress\\";
        logger.info("Current work space: " + dir);
        return dir;
    }

    private String getTargetDirectory(TaskItem taskItem) {
        String dir = this.getWorkspace(taskItem) + taskItem.getTask().getTaskNo();
        logger.info("Target directory: " + dir);
        return dir;
    }

    private String getWorkspace(AssetFile assetFile) {
        String dir = this.ffmpeg.getWorkspace() + assetFile.getAssetNo() + "\\Datalog\\";
        logger.info("Current work space: " + dir);
        return dir;
    }

    private String getTargetDirectory(AssetFile assetFile) {
        String dir = this.getWorkspace(assetFile) + "compress";
        logger.info("Target directory: " + dir);
        return dir;
    }

    private boolean safeCreateFolder(String path) {
        File target = new File(path);
        if (!target.exists()) {
            try {
                target.mkdir();
                logger.info("Create folder success:" + path);
            } catch (Exception ex) {
                logger.error("Create folder failed.", ex);
                logger.error(ExceptionUtils.getStackTrace(ex));
                return false;
            }
        }

        return true;
    }

    private List<String> buildVideoInfoCommandArgs(AssetFile assetFile) {
        List<String> arguments = new ArrayList<String>();
        arguments.add(ffmpeg.getFfprobe());
        arguments.add("-loglevel");
        arguments.add("error");
        arguments.add("-show_streams");
        arguments.add(assetFile.getFileName());
        arguments.add("-print_format");
        arguments.add("xml=x=1");
        arguments.add("-noprivate");
        return arguments;
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

}

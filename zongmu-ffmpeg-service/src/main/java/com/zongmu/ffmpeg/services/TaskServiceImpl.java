package com.zongmu.ffmpeg.services;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zongmu.ffmpeg.entities.AssetFile;
import com.zongmu.ffmpeg.entities.AssetFileStatus;
import com.zongmu.ffmpeg.entities.AssetType;
import com.zongmu.ffmpeg.entities.DeleteAsset;
import com.zongmu.ffmpeg.entities.DeleteAssetStatus;
import com.zongmu.ffmpeg.entities.FileUploadStatus;
import com.zongmu.ffmpeg.entities.Task;
import com.zongmu.ffmpeg.entities.TaskItem;
import com.zongmu.ffmpeg.entities.TaskItemFile;
import com.zongmu.ffmpeg.entities.TaskItemFileStatus;
import com.zongmu.ffmpeg.entities.TaskItemStatus;
import com.zongmu.ffmpeg.properties.FfmpegProperties;
import com.zongmu.ffmpeg.repositories.AssetFileRepo;
import com.zongmu.ffmpeg.repositories.DeleteAssetRepo;
import com.zongmu.ffmpeg.repositories.TaskItemFileRepo;
import com.zongmu.ffmpeg.repositories.TaskItemRepo;
import com.zongmu.ffmpeg.repositories.TaskRepo;
import com.zongmu.ffmpeg.task.CommandResult;
import com.zongmu.ffmpeg.task.CommandType;

@Service
public class TaskServiceImpl implements TaskService {

    private static Logger logger = Logger.getLogger(TaskServiceImpl.class);

    private static int MAX = 9999;
    private static int MIN = 1000;
    private Random random = new Random();

    @Autowired
    private TaskItemRepo taskItemRepo;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private AssetFileRepo assetFileRepo;

    @Autowired
    private TaskItemFileRepo taskItemFileRepo;
    
    @Autowired
    private FfmpegProperties ffmpeg;

    @Override
    public List<TaskItem> getPendingTaskItems(int count) {
        List<TaskItem> taskItems = this.taskItemRepo.getPendingTaskItems(count);
        for (TaskItem taskItem : taskItems) {
            taskItem.setTask(this.getTask(taskItem.getTaskId()));
            taskItem.getAssetFiles().addAll(this.assetFileRepo.getAssetFiles(taskItem.getAssetNo()));
        }
        return taskItems;
    }

    @Override
    public List<TaskItem> getFailedTaskItems(int count) {
        List<TaskItem> taskItems = this.taskItemRepo.getFailedTaskItems(count);
        for (TaskItem taskItem : taskItems) {
            taskItem.setTask(this.getTask(taskItem.getTaskId()));
            taskItem.getAssetFiles().addAll(this.assetFileRepo.getAssetFiles(taskItem.getAssetNo()));
        }
        return taskItems;
    }

    @Autowired
    private DeleteAssetRepo deleteAssetRepo;

    @Override
    public void moveDeleteAssetFiles(int count) {
        List<DeleteAsset> deleteAssets = deleteAssetRepo.getDeleteAssets(count);
        for (DeleteAsset deleteAsset : deleteAssets) {
            logger.info("Start move asset " + deleteAsset.getAssetNo() + " ...");
            try {
                deleteAsset.setStatus(DeleteAssetStatus.INPROGRESS);
                this.deleteAssetRepo.save(deleteAsset);
                String src = this.ffmpeg.getWorkspace() + deleteAsset.getAssetNo();
                String dest = this.ffmpeg.getOld();
                FileUtils.moveDirectoryToDirectory(new File(src),new File(dest), true);
                deleteAsset.setStatus(DeleteAssetStatus.FINISH);
                this.deleteAssetRepo.save(deleteAsset);
                logger.info("Move asset " + deleteAsset.getAssetNo() + " successfully.");
            } catch (Exception ex) {
                logger.error(ex);
                logger.error("Move asset " + deleteAsset.getAssetNo() + " failed.");
                deleteAsset.setStatus(DeleteAssetStatus.DIRTYDATA);
                this.deleteAssetRepo.save(deleteAsset);
            }
        }
    }

    @Override
    public List<AssetFile> getCompressFailedAssetFile(int count) {
        return this.assetFileRepo.getCompressFailedAssetFile(count);
    }

    @Override
    public List<AssetFile> getPendingAssetFiles(int count) {
        return this.assetFileRepo.getPendingAssetFile(count);
    }
    

    @Override
    public List<TaskItemFile> getPendingUploadFiles(int count) {
        List<TaskItemFile> taskItemFiles = this.taskItemFileRepo.getPendingUploadFiles(count);
        for(TaskItemFile taskItemFile : taskItemFiles){
           Task task = this.taskRepo.getTask(taskItemFile.getTaskId());
           if(task != null){
               taskItemFile.setTask(task);
           }else{
               taskItemFile.setUploadStatus(FileUploadStatus.TaskNotExist);
               this.taskItemFileRepo.save(taskItemFile);
           }
           
//           List<Task> tasks = this.taskRepo.getTaskByTaskItemNo(taskItemFile.getTaskItemNo());
//           if(tasks.size() > 1){
//               logger.info("Get more than one tasks via taskItemNo : " + taskItemFile.getTaskItemNo());
//               for(Task task: tasks){
//                   logger.info("Task Id:" + task.getId());
//                   logger.info("Task No:" + task.getTaskNo());
//               }
//               
//               logger.info("============================================");
//           }
//           
//           if(tasks.size() > 0){
//               taskItemFile.setTask(tasks.get(0));
//           }
           
        }
        return taskItemFiles;
    }

    @Override
    public void setCompressState(AssetFile assetFile) {
        assetFile.setAssetFileStatus(AssetFileStatus.COMPRESSING);
        this.assetFileRepo.save(assetFile);
    }

    @Override
    public Task getTask(String taskNo) {
        return this.taskRepo.getTask(taskNo);
    }

    @Override
    public void afterExecutionCommand(CommandResult commandResult) {
        if (commandResult.getCommandType() == CommandType.CompressVideo) {
            afterCompressVideo(commandResult);
        } else {
            afterCutVideo(commandResult);
        }
    }

    private void afterCompressVideo(CommandResult commandResult) {
        AssetFile assetFile = commandResult.getAssetFile();
        assetFile.setAssetFileStatus(commandResult.isSuccess() ? AssetFileStatus.COMPRESSSUCCESS : AssetFileStatus.COMPRESSFAILED);
        assetFile.setUpdateTime(DateTime.now());
        this.assetFileRepo.save(assetFile);
    }

    private void afterCutVideo(CommandResult commandResult) {
        TaskItem taskItem = commandResult.getTaskItem();
        if (commandResult.isSuccess()) {
            if (taskItem.getAssetType() == AssetType.FOUR) {
                Long count = this.taskItemFileRepo.getTaskItemFileSuccessCount(taskItem.getTaskItemNo());
                if (count == 3) {
                    taskItem.setStatus(TaskItemStatus.NEW);
                }
            } else {
                taskItem.setStatus(TaskItemStatus.NEW);
            }
        } else {
            taskItem.setStatus(TaskItemStatus.CREATEFAILED);
        }

        taskItem.setUpdateTime(DateTime.now());
        this.taskItemRepo.save(commandResult.getTaskItem());

        String ftpPath = String.format("%s/Datalog/compress/%s/%s", taskItem.getAssetNo(), taskItem.getTask().getTaskNo(), commandResult.getFileName());
        AssetFile assetFile = commandResult.getAssetFile();
        TaskItemFile taskItemFile = new TaskItemFile();
        taskItemFile.setTaskItemFileNo(this.generateNo());
        taskItemFile.setTaskItemNo(taskItem.getTaskItemNo());
        taskItemFile.setTaskId(taskItem.getTaskId());
        taskItemFile.setFps(assetFile.getFps());
        taskItemFile.setHeight(assetFile.getHeight());
        taskItemFile.setWidth(assetFile.getWidth());
        taskItemFile.setAssetFileNo(assetFile.getAssetFileNo());
        taskItemFile.setPath(ftpPath);
        taskItemFile.setStatus(commandResult.isSuccess() ? TaskItemFileStatus.SUCCESS : TaskItemFileStatus.FAILURE);
        this.taskItemFileRepo.save(taskItemFile);
    }

    public String generateNo() {
        return DateTime.now().toString("yyyyMMddHHmmssSSS") + randomNum();
    }

    private int randomNum() {
        return this.random.nextInt(MAX) % (MAX - MIN + 1) + MIN;
    }

    private Task getTask(Long taskId) {
        return this.taskRepo.getTask(taskId);
    }

    @Override
    public void updateAssetFile(AssetFile assetFile) {
        this.assetFileRepo.save(assetFile);
    }

    @Override
    public void updateTaskItemFile(TaskItemFile taskItemFile) {
        this.taskItemFileRepo.save(taskItemFile);
    }


}

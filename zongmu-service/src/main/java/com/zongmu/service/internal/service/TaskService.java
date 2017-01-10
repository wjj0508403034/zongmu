package com.zongmu.service.internal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.zongmu.service.criteria.QueryParams;
import com.zongmu.service.dto.asset.AssetTagParam;
import com.zongmu.service.dto.asset.ViewTagParam;
import com.zongmu.service.dto.search.HomePageSearchParam;
import com.zongmu.service.dto.search.ReportSearchParam;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.Asset2AssetViewTag;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.ffmpeg.CutFailure;
import com.zongmu.service.ffmpeg.CutResult;
import com.zongmu.service.ffmpeg.CutSuccess;
import com.zongmu.service.ffmpeg.VideoItemInfo;
import com.zongmu.service.ffmpeg.cut.TaskInfo;

public interface TaskService {

	Task createTask(Task task) throws BusinessException;

	Task getSimpleTask(String taskNo) throws BusinessException;

	void updateTask(Task task);

	TaskItem acceptTask(String taskItemNo) throws BusinessException;

	void batchAcceptTask(List<String> taskItemNos) throws BusinessException;

	Page<TaskItem> getTaskItems(QueryParams queryParams, Pageable pageable);

	List<Task> getTasksByAssetNo(String assetNo);

	void assignTaskRecord(TaskItem taskItem, TaskRecord taskRecord);

	TaskItem getTaskItem(String taskItemNo) throws BusinessException;

	void batchCreateTaskItem(TaskInfo taskInfo) throws BusinessException;

	void saveTaskItem(TaskItem taskItem);

	Task getTaskDetail(String taskNo, Pageable pageable) throws BusinessException;

	void cutSuccess(CutSuccess cutSuccess) throws BusinessException;

	void cutFailure(CutFailure cutFailure) throws BusinessException;

	void cutItemSuccess(VideoItemInfo videoItemInfo) throws BusinessException;

	void cutItemFailure(VideoItemInfo videoItemInfo) throws BusinessException;

	/*
	 * 置顶功能
	 */
	void setTop(String taskNo, boolean top) throws BusinessException;

	void setPriority(String taskNo, int priority) throws BusinessException;

	Task getTaskByTaskRecord(String taskRecordNo);

	void cutFinished(CutResult cutResult);

	void cutFailure(TaskItem taskItem);

	Long countTasksByAlgorithm(Long algorithmId);

	Long countByAssetTag(Long assetTagId);

	void setShow(String taskNo, boolean show) throws BusinessException;

	void setAssetTags(String taskNo, List<Long> tagIds) throws BusinessException;

	Long sumVideoLength(Long algorithmId, Long roadTagId, Long weatherTagId, int from, int to);

	Long countPicCount(Long algorithmId, Long roadTagId, Long weatherTagId, int from, int to);

	void updateAssetTags(String assetNo, Long roadTagId, Long weatherTagId);

	void updateAssetTags(String taskItemNo, AssetTagParam tagParam);

	Long newCountPic(Long algorithmId, List<Long> assetViewItemIds, Long viewTagId);

	Long newSumVideo(Long algorithmId, List<Long> assetViewItemIds, Long viewTagId);

	void updateAssetViewTagForAllTasks(Asset asset, List<Asset2AssetViewTag> viewTags);

	void updateViewTags(String taskItemNo, ViewTagParam tagParam);

	List<TaskItem> getTaskItemsByTaskId(Long taskId);

	void deleteTaskByAsset(Asset asset);

	void deleteTask(String taskNo) throws BusinessException;

	Long newSumVideo(ReportSearchParam reportSearchParam);

	Long newCountPic(ReportSearchParam reportSearchParam);
	
	Long calcVideoTotalLength(Long algorithmId,Long viewTagItemId,ReportSearchParam reportSearchParam);
	
	Long calcPictureTotalCount(Long algorithmId,Long viewTagItemId, ReportSearchParam reportSearchParam);

	Page<TaskItem> queryTasks(HomePageSearchParam homePageSearchParam, Pageable pageable);

	List<TaskItem> getTaskItemsForExport(Long taskId) throws BusinessException;
	
	Long countTaskItems(Long taskId);

	TaskItem getSimpleTaskItem(String taskItemNo);

	void newTask(String taskItemNo);
	
	Float getTaskItemStartTime(Task task,TaskItem taskItem);
	
	Float getTaskItemEndTime(Task task,TaskItem taskItem);
}

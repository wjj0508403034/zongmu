package com.zongmu.service.internal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.newexport.EAssetFileObject;
import com.zongmu.service.dto.newexport.EAssetObject;
import com.zongmu.service.dto.newexport.EChannelObject;
import com.zongmu.service.dto.newexport.EFrameInfoObject;
import com.zongmu.service.dto.newexport.EGeometryObject;
import com.zongmu.service.dto.newexport.EPointObject;
import com.zongmu.service.dto.newexport.EShapeObject;
import com.zongmu.service.dto.newexport.EShapeTagObject;
import com.zongmu.service.dto.newexport.ETaskItemFileObject;
import com.zongmu.service.dto.newexport.ETaskObject;
import com.zongmu.service.dto.newexport.ETaskViewTagInfoObject;
import com.zongmu.service.dto.newexport.ETaskViewTagObject;
import com.zongmu.service.dto.newexport.FrameIndexRange;
import com.zongmu.service.dto.newexport.pic.TaskXml;
import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.Asset2AssetViewTag;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.Tag;
import com.zongmu.service.entity.TagItem;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskItemFile;
import com.zongmu.service.entity.TaskItemXViewTag;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.entity.ViewTag;
import com.zongmu.service.entity.ViewTagItem;
import com.zongmu.service.entity.mark.ShapeFrameIndexInfo;
import com.zongmu.service.entity.mark.TaskMarkGroup;
import com.zongmu.service.entity.mark.TaskMarkPoint;
import com.zongmu.service.entity.mark.TaskMarkRecord;
import com.zongmu.service.entity.mark.TaskMarkRecordRefTag;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.AlgorithmService;
import com.zongmu.service.internal.service.AssetService;
import com.zongmu.service.internal.service.ColorGroupService;
import com.zongmu.service.internal.service.ReviewRecordService;
import com.zongmu.service.internal.service.TagService;
import com.zongmu.service.internal.service.TaskRecordService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.internal.service.ViewTagService;
import com.zongmu.service.internal.service.mark.TaskMarkGroupService;
import com.zongmu.service.internal.service.mark.TaskMarkRecordRefTagService;
import com.zongmu.service.internal.service.mark.TaskMarkRecordService;
import com.zongmu.service.util.FileService;

@Service
public class NewExportServiceImpl {

	private static Logger logger = Logger.getLogger(NewExportServiceImpl.class);

	@Autowired
	private AssetService assetService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskRecordService taskRecordService;

	@Autowired
	private TaskMarkRecordService taskMarkRecordService;

	@Autowired
	private TaskMarkGroupService taskMarkGroupService;

	@Autowired
	private AlgorithmService algorithmService;

	@Autowired
	private TagService tagService;

	@Autowired
	private FileService fileService;

	@Autowired
	private ReviewRecordService reviewRecordService;

	@Autowired
	private ViewTagService viewTagService;

	@Autowired
	private TaskMarkRecordRefTagService recordRefTagService;

	@Autowired
	private ColorGroupService colorGroupService;

	public EAssetObject picTasks(String assetNo, String taskNo) throws BusinessException {
		TaskXml taskXml = new TaskXml();

		Asset asset = this.assetService.getSimpleAsset(assetNo);
		List<AssetFile> assetFiles = this.assetService.getAssetFilesByAssetNo(assetNo);
		List<Asset2AssetViewTag> videoTags = this.assetService.getAssetViewTags(asset.getId());

		taskXml.setAsset(asset);
		taskXml.setAssetFiles(assetFiles);
		taskXml.setVideoTags(videoTags);

		Task task = this.taskService.getSimpleTask(taskNo);

		EAssetObject assetObj = new EAssetObject();
		Long taskCount = this.taskService.countTaskItems(task.getId());
		Long finishTaskCount = this.reviewRecordService.getFinishTaskCount(task.getId());
		if (finishTaskCount != taskCount) {
			assetObj.setTaskItems(this.taskService.getTaskItemsByTaskId(task.getId()));
			return assetObj;
			// throw new BusinessException(ErrorCode.Export_Failed);
		}

		List<TaskItem> taskItems = this.taskService.getTaskItemsForExport(task.getId());

		taskXml.setTask(task);
		taskXml.setTaskItems(taskItems);

		Algorithm algorithm = this.algorithmService.getAlgorithm(task.getAlgorithmId());

		taskXml.setAlgorithm(algorithm);

		List<ViewTag> viewTags = this.viewTagService.getSimpleAllViewTags();
		List<ViewTagItem> viewTagItems = this.viewTagService.getAllViewTagItems();

		taskXml.setViewTags(viewTags);
		taskXml.setViewTagItems(viewTagItems);

		List<TaskRecord> taskRecords = this.taskRecordService.getTaskRecordsByTaskId(task.getId());
		for (TaskRecord taskRecord : taskRecords) {
			List<TaskMarkRecord> taskMarkRecords = this.taskMarkRecordService.getSimpleRecords(taskRecord.getId());
			taskXml.addTaskMarkRecords(taskMarkRecords);

			for (TaskMarkRecord record : taskMarkRecords) {
				List<TaskMarkGroup> groups = this.taskMarkGroupService.getGroups(record.getId());
				taskXml.addGroups(groups, taskRecord);

				List<TaskMarkRecordRefTag> refTags = this.recordRefTagService.getRefTags(record.getId());
				taskXml.addRefTags(refTags);
			}
		}

		List<Tag> tags = this.tagService.getSimpleTagsByAlgorithm(algorithm.getId());
		taskXml.setTags(tags);
		for (Tag tag : tags) {
			List<TagItem> tagItems = this.tagService.getTagItemsByTagId(tag.getId());
			taskXml.addTagItems(tagItems);
		}

		// ColorGroup colorGroup =
		// this.colorGroupService.getColorGroupDetail(algorithm.getId());

		try {
			Document doc = this.newXDoc();
			doc.appendChild(taskXml.toXml(doc));
			this.fileService.saveXmlFile(doc, task, task.getTaskNo() + ".xml");
		} catch (Exception ex) {
			logger.error(ExceptionUtils.getStackTrace(ex));
			throw new BusinessException(ErrorCode.Export_Failed_System);
		}

		return null;
	}

	public EAssetObject tasks(String assetNo, String taskNo) throws BusinessException {
		Asset asset = this.assetService.getAssetWithFiles(assetNo);
		Task task = this.taskService.getSimpleTask(taskNo);
		Long taskCount = this.taskService.countTaskItems(task.getId());
		Long finishTaskCount = this.reviewRecordService.getFinishTaskCount(task.getId());
		EAssetObject assetObj = new EAssetObject();
		if (finishTaskCount != taskCount) {
			assetObj.setTaskItems(this.taskService.getTaskItemsByTaskId(task.getId()));
			return assetObj;
			// throw new BusinessException(ErrorCode.Export_Failed);
		}

		assetObj.setAssetName(asset.getName());
		assetObj.setAssetNo(asset.getAssetNo());
		assetObj.setAssetType(asset.getAssetType());
		assetObj.setVideoTags(asset.getViewTags());
		for (AssetFile assetFile : asset.getAssetFiles()) {
			assetObj.setFps(assetFile.getFps());
			assetObj.getFileMap().put(assetFile.getAssetFileNo(), this.generalAssetFileObject(assetFile));
		}

		task.setFps(assetObj.getFps());
		assetObj.setTask(generalTask(task));

		try {
			Document doc = this.newXDoc();
			doc.appendChild(assetObj.toXml(doc));
			this.fileService.saveXmlFile(doc, task, task.getTaskNo() + ".xml");
		} catch (Exception ex) {
			logger.error(ExceptionUtils.getStackTrace(ex));
			throw new BusinessException(ErrorCode.Export_Failed_System);
		}

		return assetObj;
	}

	private Document newXDoc() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder.newDocument();
	}

	private EAssetFileObject generalAssetFileObject(AssetFile assetFile) {
		EAssetFileObject fileObj = new EAssetFileObject();
		fileObj.setAssetFileNo(assetFile.getAssetFileNo());
		fileObj.setFileName(assetFile.getFileName());
		fileObj.setFileSize(assetFile.getFileSize());
		fileObj.setHeight(assetFile.getHeight());
		fileObj.setWidth(assetFile.getWidth());
		fileObj.setFps(assetFile.getFps());
		fileObj.setDuration(assetFile.getDuration());
		return fileObj;
	}

	private ETaskObject generalTask(Task task) throws BusinessException {
		ETaskObject taskObj = new ETaskObject();
		taskObj.setTaskName(task.getTaskName());
		taskObj.setTaskNo(task.getTaskNo());
		taskObj.setTaskType(task.getTaskType());
		taskObj.setShapeType(task.getShapeType());
		taskObj.setSideCount(task.getSideCount());
		taskObj.setAlgorithm(this.algorithmService.getAlgorithm(task.getAlgorithmId()));
		taskObj.setMarkTags(this.tagService.getTagsByAlgorithm(task.getAlgorithmId()));
		List<TaskItem> taskItems = this.taskService.getTaskItemsForExport(task.getId());
		for (TaskItem taskItem : taskItems) {
			TaskRecord taskRecord = this.taskRecordService.getTaskRecord(taskItem.getTaskRecordNo());
			taskRecord.setTaskMarkRecords(this.taskMarkRecordService.getRecords(taskRecord.getId()));
			taskRecord.setTaskItem(taskItem);

			taskItem.setStartTime(this.taskService.getTaskItemStartTime(task, taskItem));
			taskItem.setEndTime(this.taskService.getTaskItemEndTime(task, taskItem));

			for (TaskItemFile taskItemFile : taskItem.getTaskItemFiles()) {
				taskObj.getFileMap().put(taskItemFile.getId(), this.generalTaskItemFile(taskItemFile));
			}

			for (TaskMarkRecord taskMarkRecord : taskRecord.getTaskMarkRecords()) {
				EShapeObject shapeObject = this.generalShape(task, taskObj, taskRecord, taskMarkRecord, taskItem);
				taskObj.addShapeObject(taskItem.getOrderNo(), shapeObject);
				// taskObj.getObjects().add(this.generalShape(task, taskObj,
				// taskRecord, taskMarkRecord, taskItem));
			}

			for (TaskItemXViewTag tag : taskItem.getViewTags()) {
				String key = tag.getViewTagId() + "_" + tag.getViewTagItemId();
				if (!taskObj.getTagMap().containsKey(key)) {
					ETaskViewTagObject tagObj = new ETaskViewTagObject();
					taskObj.getTagMap().put(key, tagObj);
					tagObj.setViewTagId(tag.getViewTagId());
					tagObj.setViewTagName(tag.getViewTag().getName());
					for (ViewTagItem tagItem : tag.getViewTag().getItems()) {
						if (this.longValueEquals(tagItem.getId(), tag.getViewTagItemId())) {
							tagObj.setViewTagItem(tagItem);
							break;
						}
					}
				}

				ETaskViewTagInfoObject info = new ETaskViewTagInfoObject();
				info.setStartTime(taskItem.getStartTime());
				info.setEndTime(taskItem.getEndTime());
				info.setTaskItemId(taskItem.getId());
				taskObj.getTagMap().get(key).getInfos().add(info);
			}
		}

		return taskObj;
	}
	
	private boolean longValueEquals(Long value1,Long value2){
		if(value1 == null){
			if(value2 == null){
				return true;
			}
			
			return false;
		}
		
		if(value2 == null){
			return false;
		}
		
		return value1.longValue() == value2.longValue();
	}

	private ETaskItemFileObject generalTaskItemFile(TaskItemFile taskItemFile) {
		ETaskItemFileObject taskItemObj = new ETaskItemFileObject();
		taskItemObj.setId(taskItemFile.getId());
		taskItemObj.setAssetFileNo(taskItemFile.getAssetFileNo());
		taskItemObj.setTaskFileItemNo(taskItemFile.getTaskItemFileNo());
		return taskItemObj;
	}

	private EShapeObject generalShape(Task task, ETaskObject taskObj, TaskRecord taskRecord,
			TaskMarkRecord taskMarkRecord, TaskItem taskItem) throws BusinessException {

		Float startFrameIndex = new Float(task.getFps() * taskItem.getStartTime());
		EShapeObject shapeObj = new EShapeObject();
		shapeObj.setTaskItemNo(taskItem.getTaskItemNo());
		shapeObj.setTaskItemOrderNo(taskItem.getOrderNo());
		shapeObj.setStartRange(new FrameIndexRange(task, taskItem, true));
		shapeObj.setEndRange(new FrameIndexRange(task, taskItem, false));
		shapeObj.setName(taskMarkRecord.getName());
		shapeObj.setColor(taskMarkRecord.getColor());
		shapeObj.setColorId(taskMarkRecord.getColorTagId());
		shapeObj.setTaskItem(taskItem);
		shapeObj.setTask(task);

		for (TaskMarkRecordRefTag tag : taskMarkRecord.getTags()) {
			EShapeTagObject tagObj = new EShapeTagObject();
			for (Tag tagx : taskObj.getMarkTags()) {
				boolean find = false;
				for (TagItem tagxItem : tagx.getItems()) {
					if (tagxItem.getId().equals(tag.getTagItemId())) {
						find = true;
						tagObj.setTagId(tagx.getId());
						tagObj.setTagName(tagx.getName());
						tagObj.setTagItemId(tag.getTagItemId());
						tagObj.setTagItemName(tagxItem.getValue());
						shapeObj.getTags().add(tagObj);
						break;
					}
				}

				if (find) {
					break;
				}
			}

		}

		Map<String, EChannelObject> channelMap = new HashMap<>();
		Long fileId = null;
		if (task.getAssetType() == AssetType.SINGLE) {
			if (taskItem.getTaskItemFiles().size() > 0) {
				fileId = taskItem.getTaskItemFiles().get(0).getId();
			} else {
				throw new BusinessException(ErrorCode.Export_Failed_System);
			}
		}
		for (TaskMarkGroup group : taskMarkRecord.getGroups()) {
			if (task.getAssetType() == AssetType.SINGLE) {
				group.setTaskItemFileId(fileId);
			} else {
				fileId = group.getTaskItemFileId();
			}
			String taskFileName = this.getTaskFileName(taskItem, fileId);
			group.setTaskItemFileName(taskFileName);
			if (!channelMap.containsKey(taskFileName)) {
				channelMap.put(taskFileName, new EChannelObject());
			}
			EChannelObject channel = channelMap.get(taskFileName);
			channel.setTaskItemFileId(group.getTaskItemFileId());
			channel.setFileName(taskFileName);
			EGeometryObject geometry = this.generalEGeometry(group, taskItem, startFrameIndex.longValue());
			channel.getGeometries().add(geometry);
		}
		long taskItemStartValue = startFrameIndex.longValue();
		if (task.getAssetType() == AssetType.SINGLE) {
			EFrameInfoObject obj = new EFrameInfoObject();
			obj.setStartRange(shapeObj.getStartRange());
			obj.setEndRange(shapeObj.getEndRange());
			if (taskMarkRecord.getEndIndex() != null) {
				obj.setEndIndex(taskItemStartValue + taskMarkRecord.getEndIndex().longValue());
			} else {
				obj.setEndIndex(taskItemStartValue + this.getTaskItemEndFrameIndex(task, taskItem));
			}

			obj.setStartIndex(taskItemStartValue + taskMarkRecord.getStartIndex().longValue());
			obj.setFileId(fileId);
			String taskFileName = this.getTaskFileName(taskItem, fileId);
			obj.setTaskItemFileName(taskFileName);
			shapeObj.addFrameInfo(obj);
		} else {
			for (ShapeFrameIndexInfo info : taskMarkRecord.getShapeFrameIndexInfos()) {
				EFrameInfoObject obj = new EFrameInfoObject();
				String taskFileName = this.getTaskFileName(taskItem, info.getFileId());
				obj.setTaskItemFileName(taskFileName);
				obj.setStartRange(shapeObj.getStartRange());
				obj.setEndRange(shapeObj.getEndRange());
				if (info.getEndIndex() == null) {
					obj.setEndIndex(taskItemStartValue + this.getTaskItemEndFrameIndex(task, taskItem));
				} else {
					obj.setEndIndex(taskItemStartValue + info.getEndIndex());
				}
				obj.setStartIndex(taskItemStartValue + info.getStartIndex());
				obj.setFileId(info.getFileId());
				shapeObj.addFrameInfo(obj);
			}
		}

		for (EChannelObject channelObj : channelMap.values()) {
			shapeObj.addChannel(channelObj);
		}

		return shapeObj;
	}

	private String getTaskFileName(TaskItem taskItem, Long taskFileId) {
		for (TaskItemFile taskItemFile : taskItem.getTaskItemFiles()) {
			if (taskItemFile.getId() == taskFileId) {
				if (StringUtils.containsIgnoreCase(taskItemFile.getPath(), "front")) {
					return "front.avi";
				}

				if (StringUtils.containsIgnoreCase(taskItemFile.getPath(), "left")) {
					return "left.avi";
				}

				if (StringUtils.containsIgnoreCase(taskItemFile.getPath(), "right")) {
					return "right.avi";
				}

				if (StringUtils.containsIgnoreCase(taskItemFile.getPath(), "rear")) {
					return "rear.avi";
				}
				
				String[] paths = taskItemFile.getPath().split("/");
				if(paths.length > 0){
					return paths[paths.length - 1];
				}
			}
		}

		return "unknown";
	}

	private long getTaskItemEndFrameIndex(Task task, TaskItem taskItem) {
		Float value = 0f;
		if (taskItem.getOrderNo() != 0) {
			value = (taskItem.getVideoLength() + 0.5f) * task.getFps();
		} else {
			value = (taskItem.getVideoLength()) * task.getFps();
		}

		return value.longValue();
	}

	private EGeometryObject generalEGeometry(TaskMarkGroup group, TaskItem taskItem, long startIndex) {
		EGeometryObject geometry = new EGeometryObject();
		geometry.setFrameIndex(startIndex + group.getFrameIndex());
		geometry.setTag(0);
		for (TaskMarkPoint point : group.getPoints()) {
			geometry.getPoints().add(this.generalPoint(point));
		}
		return geometry;
	}

	private EPointObject generalPoint(TaskMarkPoint point) {
		EPointObject pointObj = new EPointObject();
		pointObj.setX(point.getX());
		pointObj.setY(point.getY());
		return pointObj;
	}

}

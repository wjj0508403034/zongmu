package com.zongmu.service.internal.service.impl;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.configuration.BusinessObjectFacade;
import com.zongmu.service.criteria.Equal;
import com.zongmu.service.criteria.Filter;
import com.zongmu.service.criteria.NotEqual;
import com.zongmu.service.criteria.OrderBy;
import com.zongmu.service.criteria.QueryParams;
import com.zongmu.service.dto.asset.AssetTagParam;
import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.asset.ViewTagParam;
import com.zongmu.service.dto.reviewrecord.ReviewRecordStatus;
import com.zongmu.service.dto.search.DateRange;
import com.zongmu.service.dto.search.HomePageSearchParam;
import com.zongmu.service.dto.search.Op;
import com.zongmu.service.dto.search.ReportSearchParam;
import com.zongmu.service.dto.search.SearchTaskStatus;
import com.zongmu.service.dto.task.TaskItemFileStatus;
import com.zongmu.service.dto.task.TaskItemStatus;
import com.zongmu.service.dto.task.TaskStatus;
import com.zongmu.service.dto.task.TaskType;
import com.zongmu.service.entity.Algorithm;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.Asset2AssetViewTag;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.AssetTag;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.Task2AssetViewTag;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskItemFile;
import com.zongmu.service.entity.TaskItemXViewTag;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.entity.TaskRefAssetTag;
import com.zongmu.service.entity.TaskXViewTag;
import com.zongmu.service.entity.User;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.ffmpeg.CutFailure;
import com.zongmu.service.ffmpeg.CutResult;
import com.zongmu.service.ffmpeg.CutSuccess;
import com.zongmu.service.ffmpeg.VideoItemInfo;
import com.zongmu.service.ffmpeg.cut.TaskInfo;
import com.zongmu.service.ffmpeg.cut.TaskItemFileInfo;
import com.zongmu.service.ffmpeg.cut.TaskItemInfo;
import com.zongmu.service.internal.service.AlgorithmService;
import com.zongmu.service.internal.service.AssetService;
import com.zongmu.service.internal.service.AssetTagService;
import com.zongmu.service.internal.service.AssetViewTagService;
import com.zongmu.service.internal.service.TaskRecordService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.internal.service.ViewTagService;
import com.zongmu.service.repository.Task2AssetViewTagRepository;
import com.zongmu.service.repository.TaskItemFileRepository;
import com.zongmu.service.repository.TaskItemRepository;
import com.zongmu.service.repository.TaskItemXViewTagRepository;
import com.zongmu.service.repository.TaskRefAssetTagRepository;
import com.zongmu.service.repository.TaskRepository;
import com.zongmu.service.repository.TaskXViewTagRepository;
import com.zongmu.service.specification.ReportSpec;
import com.zongmu.service.specification.TaskSpecification;
import com.zongmu.service.util.CommonService;
import com.zongmu.service.util.FileService;

@Service
public class TaskServiceImpl implements TaskService {

	private static Logger logger = Logger.getLogger(TaskServiceImpl.class);

	@Autowired
	private AssetService assetService;

	@Autowired
	private TaskRepository taskRepo;

	@Autowired
	private TaskItemRepository taskItemRepo;

	@Autowired
	private TaskRefAssetTagRepository taskRefAssetTagRepo;

	@Autowired
	private CommonService commonService;

	@Autowired
	private TaskRecordService taskRecordService;

	@Autowired
	private TaskItemFileRepository taskItemFileRepo;

	@Autowired
	private AssetTagService assetTagService;

	@Autowired
	private AlgorithmService algorithmService;

	@Autowired
	private TaskXViewTagRepository taskXViewTagRespo;

	@Autowired
	private Task2AssetViewTagRepository task2AssetViewTagRepo;

	@Autowired
	private FileService fileService;

	private String getTaskFolder(String assetNo, String taskNo) {
		return "/upload/" + assetNo + "/Datalog/compress/" + taskNo + "/";
	}

	@Override
	public Task createTask(Task task) throws BusinessException {
		if (this.taskRepo.checkTaskDupBeforeCreate(task.getTaskName())) {
			throw new BusinessException(ErrorCode.TASK_NAME_DUP);
		}

		if (task.getAssetType() == AssetType.FOUR || task.getAssetType() == AssetType.SINGLE) {
			if (task.getTimeInterval() <= 0) {
				throw new BusinessException(ErrorCode.Task_Time_Interval_Is_Negative);
			}
		}

		Asset asset = this.assetService.getAssetWithFiles(task.getAssetNo());
		task.setTaskNo(commonService.generateNo());
		task.setCreateTime(DateTime.now());
		task.setUpdateTime(DateTime.now());
		task.setAssetType(asset.getAssetType());
		task.setRoadTagId(asset.getRoadTagId());
		task.setWeatherTagId(asset.getWeatherTagId());
		task.setTimeOfDay(asset.getTimeOfDay());
		task.setFtpFolder(this.getTaskFolder(asset.getAssetNo(), task.getTaskNo()));

		if (asset.getAssetType() == AssetType.PICTURE) {
			task.setTaskStatus(TaskStatus.CUTFINISHED);
			task = this.taskRepo.save(task);

			for (int index = 0; index < asset.getAssetFiles().size(); index++) {
				AssetFile assetFile = asset.getAssetFiles().get(index);
				TaskItem taskItem = this.initTaskItem(task);
				taskItem.setOrderNo(index);
				this.taskItemRepo.save(taskItem);

				copyTaskItemXViewTags(taskItem.getId(), task.getViewTags());

				// 20160622001455891/front.avi_0.mp4
				String ftpPath = String.format("%s/Datalog/%s", task.getAssetNo(), assetFile.getFileName());
				this.createTaskItemFile(taskItem, assetFile, ftpPath);
			}

		} else {
			task.setVideoLength(asset.getRecordLength());
			task = this.taskRepo.save(task);
			task.setAsset(asset);
			this.createTaskItems(task, asset);
		}

		for (TaskXViewTag viewTag : task.getViewTags()) {
			viewTag.setTaskId(task.getId());
			this.taskXViewTagRespo.save(viewTag);
		}

		for (Asset2AssetViewTag assetViewTag : asset.getViewTags()) {
			Task2AssetViewTag taskViewTag = new Task2AssetViewTag();
			taskViewTag.setTaskId(task.getId());
			taskViewTag.setAssetViewTagId(assetViewTag.getAssetViewTagId());
			taskViewTag.setAssetViewTagItemId(assetViewTag.getAssetViewTagItemId());
			this.task2AssetViewTagRepo.save(taskViewTag);
		}

		return task;
	}

	@Override
	public Task getSimpleTask(String taskNo) throws BusinessException {
		return this.getTaskByTaskNo(taskNo);
	}

	@Override
	public List<TaskItem> getTaskItemsByTaskId(Long taskId) {
		return this.taskItemRepo.getTaskItems(taskId);
	}

	@Override
	public List<TaskItem> getTaskItemsForExport(Long taskId) throws BusinessException {
		List<TaskItem> taskItems = this.taskItemRepo.getTaskItems(taskId);
		for (TaskItem taskItem : taskItems) {
			taskItem.setTaskItemFiles(this.taskItemFileRepo.getTaskItemFiles(taskItem.getTaskItemNo()));
			taskItem.setViewTags(getTaskItemViewTags(taskItem.getId()));
		}
		return taskItems;
	}

	@Override
	public void updateAssetViewTagForAllTasks(Asset asset, List<Asset2AssetViewTag> viewTags) {
		List<Task> tasks = this.taskRepo.findTasks(asset.getAssetNo());
		for (Task task : tasks) {
			this.updateAssetViewTag(task, viewTags);
		}
	}

	private void updateAssetViewTag(Task task, List<Asset2AssetViewTag> viewTags) {
		this.task2AssetViewTagRepo.deleteTags(task.getId());
		for (Asset2AssetViewTag assetViewTag : viewTags) {
			Task2AssetViewTag taskViewTag = new Task2AssetViewTag();
			taskViewTag.setTaskId(task.getId());
			taskViewTag.setAssetViewTagId(assetViewTag.getAssetViewTagId());
			taskViewTag.setAssetViewTagItemId(assetViewTag.getAssetViewTagItemId());
			this.task2AssetViewTagRepo.save(taskViewTag);
		}
	}

	private void createTaskItems(Task task, Asset asset) {
		if (asset.getAssetFiles().size() > 0) {
			float duration = asset.getAssetFiles().get(0).getDuration();
			int counter = 0;
			float step = 0;

			while (step < duration) {
				TaskItem taskItem = this.initTaskItem(task);
				taskItem.setOrderNo(counter++);
				taskItem.setStatus(TaskItemStatus.CUTTING);
				if (duration - step < task.getTimeInterval()) {
					taskItem.setVideoLength(duration - step);
				} else {
					taskItem.setVideoLength(task.getTimeInterval() * 1.0f);
				}

				taskItem = this.taskItemRepo.save(taskItem);

				copyTaskItemXViewTags(taskItem.getId(), task.getViewTags());

				step += task.getTimeInterval();
				// for (AssetFile assetFile : asset.getAssetFiles()) {
				// this.threadPoolService.run(new
				// CutVideoTask(this.applicationContext, task, assetFile,
				// taskItem));
				// }
			}
		}
	}

	private void copyTaskItemXViewTags(Long taskItemId, List<TaskXViewTag> tags) {
		for (TaskXViewTag viewTag : tags) {
			TaskItemXViewTag taskItemXViewTag = new TaskItemXViewTag();
			taskItemXViewTag.setTaskItemId(taskItemId);
			taskItemXViewTag.setViewTagId(viewTag.getViewTagId());
			taskItemXViewTag.setViewTagItemId(viewTag.getViewTagItemId());
			this.taskItemXViewTagRepo.save(taskItemXViewTag);
		}
	}

	@Override
	public void cutFailure(TaskItem taskItem) {
		taskItem.setStatus(TaskItemStatus.CREATEFAILED);
		taskItem.setUpdateTime(DateTime.now());
		this.taskItemRepo.save(taskItem);
	}

	@Override
	public void cutFinished(CutResult cutResult) {
		TaskItem taskItem = this.taskItemRepo.find(cutResult.getTaskItemNo());
		if (taskItem.getStatus() != TaskItemStatus.CREATEFAILED) {
			if (!cutResult.isResult()) {
				taskItem.setStatus(TaskItemStatus.CREATEFAILED);
			} else {
				if (taskItem.getAssetType() == AssetType.FOUR) {
					int count = this.taskItemFileRepo.countSuccessFiles(taskItem.getTaskItemNo());
					if (count == 3) {
						taskItem.setStatus(TaskItemStatus.NEW);
					}
				} else {
					taskItem.setStatus(TaskItemStatus.NEW);
				}
			}
		}

		taskItem.setUpdateTime(DateTime.now());
		this.taskItemRepo.save(taskItem);
		AssetFile assetFile = this.assetService.getAssetFileByNo(cutResult.getAssetFileNo());
		if (taskItem != null && assetFile != null) {
			String ftpPath = String.format("%s/Datalog/compress/%s/%s", cutResult.getAssetNo(), cutResult.getTaskNo(),
					cutResult.getFileName());
			TaskItemFile taskItemFile = this.initTaskItemFile(taskItem);
			taskItemFile.setFps(assetFile.getFps());
			taskItemFile.setHeight(assetFile.getHeight());
			taskItemFile.setWidth(assetFile.getWidth());
			taskItemFile.setAssetFileNo(cutResult.getAssetFileNo());
			taskItemFile.setPath(ftpPath);
			taskItemFile.setStatus(cutResult.isResult() ? TaskItemFileStatus.SUCCESS : TaskItemFileStatus.FAILURE);
			this.taskItemFileRepo.save(taskItemFile);
		}

	}

	@Override
	public Page<TaskItem> getTaskItems(QueryParams queryParams, Pageable pageable) {
		if (queryParams == null) {
			queryParams = new QueryParams();
		}
		queryParams.add(new NotEqual("status", TaskItemStatus.CUTTING, TaskItemStatus.class));
		queryParams.add(new NotEqual("status", TaskItemStatus.CREATEFAILED, TaskItemStatus.class));
		queryParams.add(new Equal("showHome", true, boolean.class));
		queryParams.add(new OrderBy("top", int.class, false));
		queryParams.add(new OrderBy("priority", int.class));
		return this.taskItemRepo.findAll(this.query(queryParams), pageable);
	}

	@Override
	public Page<TaskItem> queryTasks(HomePageSearchParam homePageSearchParam, Pageable pageable) {
		TaskSpecification taskSpecification = new TaskSpecification();
		return this.taskItemRepo.findAll(taskSpecification.homePageSearch(homePageSearchParam), pageable);
	}

	@Override
	public List<Task> getTasksByAssetNo(String assetNo) {
		List<Task> tasks = this.taskRepo.findTasks(assetNo);
		for (Task task : tasks) {
			task.setFtpFolder(this.getTaskFolder(assetNo, task.getTaskNo()));
		}
		return tasks;
	}

	@Override
	public void updateTask(Task task) {
		this.taskRepo.save(task);
	}

	@Autowired
	private BusinessObjectFacade boFacade;

	@Override
	public TaskItem acceptTask(String taskItemNo) throws BusinessException {
		User user = this.boFacade.currentUser();
		TaskItem taskItem = this.getTaskItem(taskItemNo);
		if (taskItem == null) {
			throw new BusinessException(ErrorCode.TASKITEM_NOT_FOUND);
		}
		
		Long rejectTaskCount = this.taskRecordService.countRejectRecords();
		if (rejectTaskCount >= 10) {
			throw new BusinessException(ErrorCode.REJECT_TASK_COUNT_LARGER_THAN_10);
		}

		if (taskItem.getStatus() != TaskItemStatus.NEW) {
			throw new BusinessException(ErrorCode.Task_Accept_By_Others);
		}
		
		taskItem.setStatus(TaskItemStatus.INPROGRESS);
		this.saveTaskItem(taskItem);
		
		if (taskItem.getTaskRecordNo() == null) {
			TaskRecord taskRecord = this.taskRecordService.createNewTaskRecord(taskItem);
			taskItem.setTaskRecordNo(taskRecord.getTaskRecordNo());
			this.saveTaskItem(taskItem);
		} else {
			TaskRecord taskRecord = this.taskRecordService.getTaskRecord(taskItem.getTaskRecordNo());
			taskRecord.setUserId(user.getId());
			taskRecord.setUpdateTime(DateTime.now());
			this.taskRecordService.save(taskRecord);
			taskItem.setStatus(TaskItemStatus.INPROGRESS);
			this.taskItemRepo.save(taskItem);
		}

		return taskItem;
	}

	@Override
	public void batchAcceptTask(List<String> taskItemNos) throws BusinessException {
		Long rejectTaskCount = this.taskRecordService.countRejectRecords();
		if (rejectTaskCount >= 10) {
			throw new BusinessException(ErrorCode.REJECT_TASK_COUNT_LARGER_THAN_10);
		}

		for (String taskItemNo : taskItemNos) {
			TaskItem taskItem = this.getTaskItem(taskItemNo);
			if (taskItem != null) {
				TaskRecord taskRecord = this.taskRecordService.createNewTaskRecord(taskItem);
				this.assignTaskRecord(taskItem, taskRecord);
			}
		}
	}

	@Override
	public void assignTaskRecord(TaskItem taskItem, TaskRecord taskRecord) {
		taskItem.setTaskRecordNo(taskRecord.getTaskRecordNo());
		taskItem.setStatus(TaskItemStatus.INPROGRESS);
		this.saveTaskItem(taskItem);
	}

	@Override
	public TaskItem getTaskItem(String taskItemNo) throws BusinessException {
		TaskItem taskItem = this.taskItemRepo.find(taskItemNo);
		Task task = this.taskRepo.findOne(taskItem.getTaskId());
		taskItem.setTaskItemFiles(this.taskItemFileRepo.getTaskItemFiles(taskItem.getTaskItemNo()));
		taskItem.setViewTags(getTaskItemViewTags(taskItem.getId()));
		taskItem.setFtpFolder(this.fileService.getFTPPath(task));
		return taskItem;
	}

	@Override
	public void saveTaskItem(TaskItem taskItem) {
		this.taskItemRepo.save(taskItem);
	}

	@Override
	public Task getTaskDetail(String taskNo, Pageable pageable) throws BusinessException {
		Task task = this.getTaskByTaskNo(taskNo);
		Algorithm algorithm = this.algorithmService.getAlgorithm(task.getAlgorithmId());
		task.setAlgorithmName(algorithm.getName());
		task.setTaskItems(this.getTaskItems(task, pageable));
		task.setWeatherTag(this.assetTagService.getAssetTag(task.getWeatherTagId()));
		task.setRoadTag(this.assetTagService.getAssetTag(task.getRoadTagId()));
		task.setAssetViewTags(getAssetViewTags(task.getId()));
		task.setViewTags(getViewTags(task.getId()));
		task.setFtpFolder(this.fileService.getFTPPath(task));
		return task;
	}

	@Autowired
	private ViewTagService viewTagService;

	private List<TaskXViewTag> getViewTags(Long taskId) throws BusinessException {
		List<TaskXViewTag> tags = this.taskXViewTagRespo.getListByTaskId(taskId);
		for (TaskXViewTag tag : tags) {
			tag.setViewTag(this.viewTagService.getViewTagDetail(tag.getViewTagId()));
		}
		return tags;
	}

	private List<TaskItemXViewTag> getTaskItemViewTags(Long taskItemId) throws BusinessException {
		List<TaskItemXViewTag> tags = this.taskItemXViewTagRepo.getTags(taskItemId);
		for (TaskItemXViewTag tag : tags) {
			tag.setViewTag(this.viewTagService.getViewTagDetail(tag.getViewTagId()));
		}
		return tags;
	}

	@Autowired
	private AssetViewTagService assetViewTagService;

	private List<Task2AssetViewTag> getAssetViewTags(Long taskId) {
		List<Task2AssetViewTag> tags = this.task2AssetViewTagRepo.getListByTaskId(taskId);
		for (Task2AssetViewTag tag : tags) {
			tag.setViewTag(assetViewTagService.getAssetViewTag(tag.getAssetViewTagId()));
		}
		return tags;
	}

	@Override
	public void batchCreateTaskItem(TaskInfo taskInfo) throws BusinessException {
		Task task = this.getTaskByTaskNo(taskInfo.getTaskNo());
		task.setSubTotal(taskInfo.getTaskItemInfos().size());
		task.setTaskStatus(TaskStatus.CUTFINISHED);
		this.taskRepo.save(task);

		for (TaskItemInfo taskItemInfo : taskInfo.getTaskItemInfos()) {
			TaskItem taskItem = this.initTaskItem(task);
			taskItem.setOrderNo(taskItemInfo.getOrder());
			// taskItem.setFps(taskInfo.getFps());
			this.taskItemRepo.save(taskItem);

			for (TaskItemFileInfo taskItemFileInfo : taskItemInfo.getTaskItemFileInfos()) {
				TaskItemFile taskItemFile = this.initTaskItemFile(taskItem);
				taskItemFile.setAssetFileNo(taskItemFileInfo.getAssetFileNo());
				// taskItemFile.setHeight(taskItemFileInfo.get);
				// taskItemFile.setWidth(taskInfo.getWidth());
				// 20160622001455891/compress/20160622005241359/front.avi_0.mp4
				String path = String.format("%s/Datalog/compress/%s/%s", task.getAssetNo(), task.getTaskNo(),
						taskItemFileInfo.getFileName());
				taskItemFile.setPath(path);
				this.taskItemFileRepo.save(taskItemFile);
			}
		}

	}

	@Override
	public void cutSuccess(CutSuccess cutSuccess) throws BusinessException {
		Task task = this.getTaskByTaskNo(cutSuccess.getTaskNo());
		task.setSubTotal(cutSuccess.getTotal());
		task.setTaskStatus(TaskStatus.CUTFINISHED);
		this.taskRepo.save(task);
	}

	@Override
	public void cutFailure(CutFailure cutFailure) throws BusinessException {
		Task task = this.getTaskByTaskNo(cutFailure.getTaskNo());
		task.setTaskStatus(TaskStatus.CUTTINGFAILURE);
		this.taskRepo.save(task);
	}

	@Override
	public void cutItemSuccess(VideoItemInfo videoItemInfo) throws BusinessException {
		Task task = this.getTaskByTaskNo(videoItemInfo.getTaskNo());
		TaskItem taskItem = this.newTaskItem(task, videoItemInfo);
		taskItem.setStatus(TaskItemStatus.NEW);
		this.taskItemRepo.save(taskItem);
	}

	@Override
	public void cutItemFailure(VideoItemInfo videoItemInfo) throws BusinessException {
		Task task = this.getTaskByTaskNo(videoItemInfo.getTaskNo());
		TaskItem taskItem = this.newTaskItem(task, videoItemInfo);
		taskItem.setStatus(TaskItemStatus.CREATEFAILED);
		this.taskItemRepo.save(taskItem);
	}

	private TaskItem newTaskItem(Task task, VideoItemInfo videoItemInfo) {
		// TaskItem taskItem = new TaskItem();
		// taskItem.setTaskId(task.getId());
		// taskItem.setTaskName(task.getTaskName());
		// taskItem.setTaskItemNo(this.commonService.generateNo());
		// taskItem.setOrderNo(videoItemInfo.getOrder());
		// taskItem.setFps(videoItemInfo.getFps());
		// taskItem.setTaskType(TaskType.VIDEO);
		// taskItem.setPoint(task.getPoint());
		// taskItem.setShapeType(task.getShapeType());
		// taskItem.setSideCount(task.getSideCount());
		// taskItem.setSrc("http://www.w3school.com.cn/i/movie.mp4");
		// taskItem.setFileName(videoItemInfo.getFileName());
		// return taskItem;
		return null;
	}

	private Task getTaskByTaskNo(String taskNo) throws BusinessException {
		Task task = this.taskRepo.getTask(taskNo);
		if (task == null) {
			logger.error("Task No:" + taskNo);
			throw new BusinessException(ErrorCode.TASK_NOT_FOUND);
		}

		task.setFtpFolder(this.getTaskFolder(task.getAssetNo(), task.getTaskNo()));

		return task;
	}

	@Override
	public Task getTaskByTaskRecord(String taskRecordNo) {
		return this.taskRepo.getTaskByTaskRecordNo(taskRecordNo);
	}

	private Page<TaskItem> getTaskItems(Task task, Pageable pageable) {
		Page<TaskItem> taskItems = this.taskItemRepo.getTaskItems(task.getId(), pageable);
		if (task.getTaskType() == TaskType.VIDEO) {
			for (TaskItem taskItem : taskItems.getContent()) {
				taskItem.setStartTime(this.getTaskItemStartTime(task, taskItem));
				taskItem.setEndTime(this.getTaskItemEndTime(task, taskItem));
			}
		}
		return taskItems;
	}

	@Override
	public Float getTaskItemStartTime(Task task, TaskItem taskItem) {
		if (taskItem.getOrderNo() == 0) {
			return 0f;
		}
		return task.getTimeInterval() * taskItem.getOrderNo() * 1.0f - 0.5f;
	}

	@Override
	public Float getTaskItemEndTime(Task task, TaskItem taskItem) {
		if (taskItem.getOrderNo() == 0) {
			return taskItem.getVideoLength();
		}

		return task.getTimeInterval() * taskItem.getOrderNo() * 1.0f + taskItem.getVideoLength();
	}

	@Override
	public void setTop(String taskNo, boolean top) throws BusinessException {
		Task task = this.getTaskByTaskNo(taskNo);
		task.setTop(top);
		task = this.taskRepo.save(task);
		List<TaskItem> taskItems = this.taskItemRepo.getTaskItems(task.getId());
		for (TaskItem taskItem : taskItems) {
			taskItem.setTop(top);
			this.taskItemRepo.save(taskItem);
		}
	}

	@Override
	public void setShow(String taskNo, boolean show) throws BusinessException {
		Task task = this.getTaskByTaskNo(taskNo);
		task.setShowHome(show);
		task = this.taskRepo.save(task);
		List<TaskItem> taskItems = this.taskItemRepo.getTaskItems(task.getId());
		for (TaskItem taskItem : taskItems) {
			taskItem.setShowHome(show);
			this.taskItemRepo.save(taskItem);
		}
	}

	@Override
	public void setPriority(String taskNo, int priority) throws BusinessException {
		Task task = this.getTaskByTaskNo(taskNo);
		task.setPriority(priority);
		task = this.taskRepo.save(task);
		List<TaskItem> taskItems = this.taskItemRepo.getTaskItems(task.getId());
		for (TaskItem taskItem : taskItems) {
			taskItem.setPriority(priority);
			this.taskItemRepo.save(taskItem);
		}
	}

	private TaskItem initTaskItem(Task task) {
		TaskItem taskItem = new TaskItem();
		taskItem.setTaskId(task.getId());
		taskItem.setAssetNo(task.getAssetNo());
		taskItem.setTaskName(task.getTaskName());
		taskItem.setTaskItemNo(this.commonService.generateNo());
		taskItem.setStatus(TaskItemStatus.NEW);
		taskItem.setTaskType(task.getTaskType());
		taskItem.setPoint(task.getPoint());
		taskItem.setShapeType(task.getShapeType());
		taskItem.setSideCount(task.getSideCount());
		taskItem.setAssetType(task.getAssetType());
		taskItem.setCreateTime(DateTime.now());
		taskItem.setUpdateTime(DateTime.now());
		taskItem.setTop(task.isTop());
		taskItem.setPriority(task.getPriority());
		taskItem.setRoadTagId(task.getRoadTagId());
		taskItem.setWeatherTagId(task.getWeatherTagId());
		taskItem.setAlgorithmId(task.getAlgorithmId());

		return taskItem;
	}

	private void createTaskItemFile(TaskItem taskItem, AssetFile assetFile, String ftpPath) {
		TaskItemFile taskItemFile = this.initTaskItemFile(taskItem);
		taskItemFile.setAssetFileNo(assetFile.getAssetFileNo());
		taskItemFile.setFps(assetFile.getFps());
		taskItemFile.setPath(ftpPath);
		taskItemFile.setTaskId(taskItem.getTaskId());
		this.taskItemFileRepo.save(taskItemFile);
	}

	private TaskItemFile initTaskItemFile(TaskItem taskItem) {
		TaskItemFile taskItemFile = new TaskItemFile();
		taskItemFile.setTaskItemFileNo(this.commonService.generateNo());
		taskItemFile.setTaskItemNo(taskItem.getTaskItemNo());

		return taskItemFile;
	}

	private Specification<TaskItem> query(final QueryParams queryParams) {
		return new Specification<TaskItem>() {
			@Override
			public Predicate toPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> whereList = new ArrayList<>();
				for (Filter filter : queryParams.getFilters()) {
					Predicate predicate = filter.toPredicate(root, query, cb);
					if (predicate != null) {
						whereList.add(predicate);
					}
				}

				if (whereList.size() != 0) {
					Predicate[] predicates = new Predicate[whereList.size()];
					query = query.where(whereList.toArray(predicates));
				}

				List<Order> orderList = new ArrayList<>();
				for (OrderBy orderBy : queryParams.getOrders()) {
					Order order = orderBy.toOrder(root, cb);
					if (order != null) {
						orderList.add(order);
					}
				}

				if (orderList.size() != 0) {
					Order[] orders = new Order[orderList.size()];
					query = query.orderBy(orderList.toArray(orders));
				}

				return query.getRestriction();
			}
		};
	}

	@Override
	public Long countTasksByAlgorithm(Long algorithmId) {
		return this.taskRepo.countTasksByAlgorithm(algorithmId);
	}

	@Override
	public Long countByAssetTag(Long assetTagId) {
		return this.taskRefAssetTagRepo.countByAssetTag(assetTagId);
	}

	@Transactional
	@Override
	public void setAssetTags(String taskNo, List<Long> tagIds) throws BusinessException {
		Task task = this.getTaskByTaskNo(taskNo);
		this.taskRefAssetTagRepo.deleteTags(task.getId());
		this.attachTagsToTask(task, tagIds);
	}

	private void attachTagsToTask(Task task, List<Long> tagIds) {
		for (Long tagId : tagIds) {
			AssetTag assetTag = this.assetTagService.getAssetTag(tagId);
			if (assetTag == null) {
				logger.warn("Cannot find asset tag by Id " + tagId);
			} else {
				TaskRefAssetTag taskRef = new TaskRefAssetTag();
				taskRef.setTaskId(task.getId());
				taskRef.setAssetTagId(assetTag.getId());
				taskRef.setAssetTagName(assetTag.getName());
				this.taskRefAssetTagRepo.save(taskRef);
			}
		}
	}

	@Override
	public Long sumVideoLength(Long algorithmId, Long roadTagId, Long weatherTagId, int from, int to) {
		return this.taskRepo.sum(algorithmId, roadTagId, weatherTagId, from, to);
	}

	@Override
	public Long countPicCount(Long algorithmId, Long roadTagId, Long weatherTagId, int from, int to) {
		return this.taskRepo.count(algorithmId, roadTagId, weatherTagId, from, to);
	}

	@Override
	public Long newCountPic(Long algorithmId, List<Long> assetViewItemIds, Long viewTagId) {
		if (assetViewItemIds.isEmpty()) {
			return this.taskRepo.newCountPicture(algorithmId, viewTagId);
		}
		return this.taskRepo.newCountPicture(algorithmId, assetViewItemIds, viewTagId);
	}

	@Override
	public Long newSumVideo(Long algorithmId, List<Long> assetViewItemIds, Long viewTagId) {
		if (assetViewItemIds.isEmpty()) {
			return this.taskRepo.newSumVideo(algorithmId, viewTagId);
		}
		return this.taskRepo.newSumVideo(algorithmId, assetViewItemIds, viewTagId);
	}

	@Override
	public Long newSumVideo(ReportSearchParam reportSearchParam) {
		return 0l;
		/*
		 * if (reportSearchParam.getViewTagItemIds().size() > 0) { Map<Long,
		 * ViewTagItem> map = new HashMap<>(); for (ViewTagItem tagItem :
		 * reportSearchParam.getViewTag().getItems()) { map.put(tagItem.getId(),
		 * tagItem); }
		 * 
		 * if (map.containsKey(reportSearchParam.getCurrentViewTagItemId())) {
		 * Map<Long, List<Long>> viewTagIdMap = new HashMap<>(); for (Long id :
		 * reportSearchParam.getViewTagItemIds()) { if (map.containsKey(id)) {
		 * ViewTagItem tagItem = map.get(id); if
		 * (!viewTagIdMap.containsKey(tagItem.getViewTagId())) {
		 * viewTagIdMap.put(tagItem.getViewTagId(), new ArrayList<Long>()); }
		 * List<Long> array = viewTagIdMap.get(tagItem.getViewTagId());
		 * array.add(id); } }
		 * 
		 * List<Long> xx =
		 * viewTagIdMap.get(map.get(reportSearchParam.getCurrentViewTagItemId())
		 * .getViewTagId()); if (xx != null && xx.size() != 0 &&
		 * !reportSearchParam.getViewTagItemIds()
		 * .contains(reportSearchParam.getCurrentViewTagItemId())) { return 0l;
		 * } }
		 * 
		 * }
		 * 
		 * Long sum = 0l; List<TaskItem> taskItems =
		 * this.taskItemRepo.findAll(this.queryTaskItems(reportSearchParam,
		 * TaskType.VIDEO)); for (TaskItem taskItem : taskItems) { sum +=
		 * taskItem.getVideoLength().longValue(); } // return sum;
		 */
	}

	@Override
	public Long calcVideoTotalLength(Long algorithmId, Long viewTagItemId, ReportSearchParam params) {
		ReportSpec specification = new ReportSpec();
		List<TaskItem> taskItems = this.taskItemRepo
				.findAll(specification.search(params, algorithmId, viewTagItemId, TaskType.VIDEO));

		Long sum = 0l;
		for (TaskItem taskItem : taskItems) {
			sum += taskItem.getVideoLength().longValue();
		}
		return sum;
	}

	@Override
	public Long calcPictureTotalCount(Long algorithmId, Long viewTagItemId, ReportSearchParam params) {
		ReportSpec specification = new ReportSpec();
		return this.taskItemRepo.count(specification.search(params, algorithmId, viewTagItemId, TaskType.PICTURE));
	}

	@Override
	public Long newCountPic(ReportSearchParam reportSearchParam) {
		// if (reportSearchParam == null) {
		// return this.taskRepo.newCountPicture(algorithmId, viewTagId);
		// } else {
		// return
		// this.taskItemRepo.count(this.queryPicCount(reportSearchParam));
		// }

		// if (reportSearchParam.getViewTagItemIds().size() > 0) {
		// if
		// (!reportSearchParam.getViewTagItemIds().contains(reportSearchParam.getCurrentViewTagItemId()))
		// {
		// return 0l;
		// }
		// }

		return this.taskItemRepo.count(this.queryTaskItems(reportSearchParam, TaskType.PICTURE));
	}

	private static Subquery<Long> taskIdSubQueryForReport(final ReportSearchParam params, CriteriaQuery<?> query,
			CriteriaBuilder cb) {
		Subquery<Long> subQuery = null;
		if (params.getAssetViewItemIds().size() > 0) {
			// Task Ids
			subQuery = query.subquery(Long.class);
			Root<Task2AssetViewTag> subRoot = subQuery.from(Task2AssetViewTag.class);
			subQuery.select(subRoot.get("taskId").as(Long.class));
			List<Predicate> subPredicates = new ArrayList<>();

			// assetViewTagItemId

			Predicate assetViewTagItemIdPredicate = subRoot.get("assetViewTagItemId").as(Long.class)
					.in(params.getAssetViewItemIds());
			subPredicates.add(assetViewTagItemIdPredicate);

			subQuery = subQuery.where(toPredicateArray(subPredicates));
		}

		return subQuery;
	}

	/*
	 * private static Subquery<Long> taskItemIdSubQueryForReport(final
	 * ReportSearchParam params, CriteriaQuery<?> query, CriteriaBuilder cb) {
	 * 
	 * 
	 * Map<Long, ViewTagItem> map = new HashMap<>(); for (ViewTagItem tagItem :
	 * params.getViewTag().getItems()) { map.put(tagItem.getId(), tagItem); }
	 * 
	 * Map<Long, List<Long>> viewTagIdMap = new HashMap<>(); for (Long id :
	 * params.getViewTagItemIds()) { if (map.containsKey(id)) { if
	 * (params.getCurrentViewTagItemId() != id) { ViewTagItem tagItem =
	 * map.get(id); if (!viewTagIdMap.containsKey(tagItem.getViewTagId())) {
	 * viewTagIdMap.put(tagItem.getViewTagId(), new ArrayList<Long>()); }
	 * List<Long> array = viewTagIdMap.get(tagItem.getViewTagId());
	 * array.add(id); } } }
	 * 
	 * Subquery<Long> subQuery = query.subquery(Long.class);
	 * Root<TaskItemXViewTag> subRoot = subQuery.from(TaskItemXViewTag.class);
	 * subQuery.select(subRoot.get("taskItemId").as(Long.class));
	 * List<Predicate> subPredicates = new ArrayList<>(); Predicate
	 * currentViewTagItemIdPredicate =
	 * cb.equal(subRoot.get("viewTagItemId").as(Long.class),
	 * params.getCurrentViewTagItemId());
	 * subPredicates.add(currentViewTagItemIdPredicate); if
	 * (params.getViewTagItemIds().size() > 0) { for (Long viewTagId :
	 * viewTagIdMap.keySet()) { if (viewTagIdMap.get(viewTagId).size() > 0) {
	 * Subquery<Long> existQuery = subQuery.subquery(Long.class);
	 * Root<TaskItemXViewTag> existRoot =
	 * existQuery.from(TaskItemXViewTag.class);
	 * existQuery.select(subRoot.get("taskItemId").as(Long.class));
	 * List<Predicate> existPredicates = new ArrayList<>(); //
	 * subRoot.get("taskItemId").as(Long.class).in(currentViewIdQuery);
	 * Predicate tagItemIdPredicate =
	 * existRoot.get("viewTagItemId").as(Long.class)
	 * .in(viewTagIdMap.get(viewTagId));
	 * existPredicates.add(tagItemIdPredicate); Predicate equal =
	 * cb.equal(existRoot.get("taskItemId").as(Long.class),
	 * subRoot.get("taskItemId").as(Long.class)); existPredicates.add(equal);
	 * existQuery = existQuery.where(toPredicateArray(existPredicates));
	 * Predicate existPredicate = cb.exists(existQuery);
	 * subPredicates.add(existPredicate); } } }
	 * 
	 * // if (params.getQueryTagItemIds().size() > 0) { // for (Long id :
	 * params.getQueryTagItemIds()) { // if (id !=
	 * params.getCurrentViewTagItemId()) { // Subquery<Long> existQuery =
	 * subQuery.subquery(Long.class); // Root<TaskItemXViewTag> existRoot = //
	 * existQuery.from(TaskItemXViewTag.class); //
	 * existQuery.select(subRoot.get("taskItemId").as(Long.class)); //
	 * List<Predicate> existPredicates = new ArrayList<>(); // Predicate
	 * tagItemIdPredicate = //
	 * cb.equal(existRoot.get("viewTagItemId").as(Long.class), // id); //
	 * existPredicates.add(tagItemIdPredicate); // Predicate equal = //
	 * cb.equal(existRoot.get("taskItemId").as(Long.class), //
	 * subRoot.get("taskItemId").as(Long.class)); // existPredicates.add(equal);
	 * // existQuery = existQuery.where(toPredicateArray(existPredicates)); //
	 * Predicate existPredicate = cb.exists(existQuery); //
	 * subPredicates.add(existPredicate); // } // } // }
	 * 
	 * subQuery = subQuery.where(toPredicateArray(subPredicates)); return
	 * subQuery;
	 * 
	 * // Subquery<Long> currentViewIdQuery = query.subquery(Long.class); //
	 * Root<TaskItemXViewTag> currentViewIdQueryRoot = //
	 * currentViewIdQuery.from(TaskItemXViewTag.class); //
	 * currentViewIdQuery.select(currentViewIdQueryRoot.get("taskItemId").as(
	 * Long.class)); // Predicate currentViewTagItemIdPredicate = //
	 * cb.equal(currentViewIdQueryRoot.get("viewTagItemId").as(Long.class), //
	 * params.getCurrentViewTagItemId()); // currentViewIdQuery = //
	 * currentViewIdQuery.where(currentViewTagItemIdPredicate); // // //
	 * taskItemId Ids // Subquery<Long> subQuery = query.subquery(Long.class);
	 * // Root<TaskItemXViewTag> subRoot = //
	 * subQuery.from(TaskItemXViewTag.class); //
	 * subQuery.select(subRoot.get("taskItemId").as(Long.class)); //
	 * List<Predicate> subPredicates = new ArrayList<>(); // // Predicate
	 * taskItemPredicate = //
	 * subRoot.get("taskItemId").as(Long.class).in(currentViewIdQuery); //
	 * subPredicates.add(taskItemPredicate); // // // assetViewTagItemId // if
	 * (params.getViewTagItemIds().size() > 0) { // List<Long> list = new
	 * ArrayList<>(); // for (Long id : params.getViewTagItemIds()) { // if (id
	 * != params.getCurrentViewTagItemId()) { // list.add(id); // } // } // if
	 * (list.size() > 0) { // Predicate viewTagItemIdPredicate = //
	 * subRoot.get("viewTagItemId").as(Long.class).in(list); //
	 * subPredicates.add(viewTagItemIdPredicate); // } // // } // // subQuery =
	 * subQuery.where(toPredicateArray(subPredicates)); // // return subQuery; }
	 */

	private static Subquery<String> reviewStatusQueryForReport(ReviewRecordStatus status, CriteriaQuery<?> query,
			CriteriaBuilder cb) {
		Subquery<String> subQuery = query.subquery(String.class);
		Root<ReviewRecord> subRoot = subQuery.from(ReviewRecord.class);
		subQuery.select(subRoot.get("taskItemNo").as(String.class));
		Predicate predicate = cb.equal(subRoot.get("status").as(ReviewRecordStatus.class), status);
		return subQuery.where(predicate);
	}

	private static Predicate taskCreateDateQueryForReport(Root<TaskItem> root, final ReportSearchParam params,
			CriteriaQuery<?> query, CriteriaBuilder cb) {
		if (params.getTaskDate().getFrom() != null) {
			if (params.getTaskDate().getTo() != null) {
				return cb.between(root.get("createTime").as(DateTime.class), params.getTaskDate().getFrom(),
						params.getTaskDate().getTo());
			} else {
				cb.greaterThan(root.get("createTime").as(DateTime.class), params.getTaskDate().getFrom());
			}

		} else {
			if (params.getTaskDate().getTo() != null) {
				cb.lessThan(root.get("createTime").as(DateTime.class), params.getTaskDate().getTo());
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Predicate dateRangeQuery(Root root, CriteriaBuilder cb, String fieldName, DateRange range) {
		if (range.getFrom() != null) {
			if (range.getTo() != null) {
				return cb.between(root.get(fieldName).as(DateTime.class), range.getFrom(), range.getTo());
			} else {
				return cb.greaterThan(root.get(fieldName).as(DateTime.class), range.getFrom());
			}

		} else {
			if (range.getTo() != null) {
				return cb.lessThan(root.get(fieldName).as(DateTime.class), range.getTo());
			}
		}
		return null;
	}

	private static Subquery<String> taskFinishSubQueryForReport(ReportSearchParam params, CriteriaQuery<?> query,
			CriteriaBuilder cb) {
		Subquery<String> subQuery = query.subquery(String.class);
		Root<ReviewRecord> subRoot = subQuery.from(ReviewRecord.class);
		subQuery.select(subRoot.get("taskItemNo").as(String.class));
		Predicate datePredicate = dateRangeQuery(subRoot, cb, "endTime", params.getTaskFinishDate());
		if (datePredicate != null) {
			return subQuery.where(datePredicate);
		}

		return null;
	}

	private static Subquery<String> assetIdSubQueryForReport(final ReportSearchParam params, CriteriaQuery<?> query,
			CriteriaBuilder cb) {
		// taskItemId Ids
		Subquery<String> subQuery = query.subquery(String.class);
		Root<Asset> subRoot = subQuery.from(Asset.class);
		subQuery.select(subRoot.get("assetNo").as(String.class));
		List<Predicate> subPredicates = new ArrayList<>();

		Predicate uploadDatePredicate = dateRangeQuery(subRoot, cb, "createTime", params.getUploadDate());
		if (uploadDatePredicate != null) {
			subPredicates.add(uploadDatePredicate);
		}

		if (!StringUtils.isEmpty(params.getAssetName())) {
			// TaskName : taskItem.taskName like ?1
			Predicate assetNamePredicate = cb.like(subRoot.get("name").as(String.class),
					"%" + params.getAssetName() + "%");
			subPredicates.add(assetNamePredicate);
		}

		if (params.getRecordLength() != null) {
			Predicate recordLengthPredicate = null;
			if (params.getRecordLength().getOp() == Op.LessThan) {
				recordLengthPredicate = cb.lessThan(subRoot.get("recordLength").as(Float.class),
						params.getRecordLength().getValue() * 1.0f);
			} else if (params.getRecordLength().getOp() == Op.LessThan) {
				recordLengthPredicate = cb.equal(subRoot.get("recordLength").as(Float.class),
						params.getRecordLength().getValue() * 1.0f);
			} else if (params.getRecordLength().getOp() == Op.GreatThan) {
				recordLengthPredicate = cb.greaterThan(subRoot.get("recordLength").as(Float.class),
						params.getRecordLength().getValue() * 1.0f);
			}

			if (recordLengthPredicate != null) {
				subPredicates.add(recordLengthPredicate);
			}
		}

		if (subPredicates.size() > 0) {
			return subQuery.where(toPredicateArray(subPredicates));
		}

		return null;
	}

	/*
	 * 
	 * @Query(
	 * "select count(taskItem) from TaskItem taskItem where taskItem.taskType = 1 and taskItem.algorithmId = ?1 "
	 * and taskItem.taskId in (select assetViewTag.taskId from Task2AssetViewTag
	 * assetViewTag where assetViewTag.assetViewTagItemId in (?2)) and
	 * taskItem.id in (select viewTag.taskItemId from TaskItemXViewTag viewTag
	 * where viewTag.viewTagItemId = ?3)
	 */

	private Specification<TaskItem> queryTaskItems(final ReportSearchParam params, final TaskType taskType) {
		return new Specification<TaskItem>() {
			@Override
			public Predicate toPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();

				// Task Type : taskItem.taskType = 1
				Predicate taskPredicate = cb.equal(root.get("taskType").as(TaskType.class), taskType);
				predicates.add(taskPredicate);

				// AlgorithmId : taskItem.algorithmId = ?1
				Predicate algorithmIdPredicate = cb.equal(root.get("algorithmId").as(Long.class),
						params.getAlgorithmId());
				predicates.add(algorithmIdPredicate);

				Predicate taskCreateDatePredeicate = taskCreateDateQueryForReport(root, params, query, cb);
				if (taskCreateDatePredeicate != null) {
					predicates.add(taskCreateDatePredeicate);
				}

				if (params.getTaskStatus() == SearchTaskStatus.New) {
					Predicate predicate = cb.equal(root.get("status").as(TaskItemStatus.class), TaskItemStatus.NEW);
					predicates.add(predicate);
				} else if (params.getTaskStatus() == SearchTaskStatus.Accept) {
					List<TaskItemStatus> list = new ArrayList<>();
					list.add(TaskItemStatus.INPROGRESS);
					list.add(TaskItemStatus.FINISHED);
					Predicate predicate = root.get("status").as(TaskItemStatus.class).in(list);
					predicates.add(predicate);
				} else if (params.getTaskStatus() == SearchTaskStatus.Pass) {
					Predicate predicate = root.get("taskItemNo").as(String.class)
							.in(reviewStatusQueryForReport(ReviewRecordStatus.PASS, query, cb));
					predicates.add(predicate);
				} else if (params.getTaskStatus() == SearchTaskStatus.Reject) {
					Predicate predicate = root.get("taskItemNo").as(String.class)
							.in(reviewStatusQueryForReport(ReviewRecordStatus.FAILED, query, cb));
					predicates.add(predicate);
				}

				if (!StringUtils.isEmpty(params.getTaskName())) {
					// TaskName : taskItem.taskName like ?1
					Predicate taskNamePredicate = cb.like(root.get("taskName").as(String.class),
							"%" + params.getTaskName() + "%");
					predicates.add(taskNamePredicate);
				}

				Subquery<String> assetSubQuery = assetIdSubQueryForReport(params, query, cb);
				if (assetSubQuery != null) {
					Predicate taskIdsPredicate = root.get("assetNo").as(String.class).in(assetSubQuery);
					predicates.add(taskIdsPredicate);
				}

				Subquery<String> taskFinishSubQuery = taskFinishSubQueryForReport(params, query, cb);
				if (taskFinishSubQuery != null) {
					Predicate predicate = root.get("taskItemNo").as(String.class).in(taskFinishSubQuery);
					predicates.add(predicate);
				}

				// taskItem.taskId in (select assetViewTag.taskId from
				// Task2AssetViewTag assetViewTag where
				// assetViewTag.assetViewTagItemId in (?2))
				Subquery<Long> taskIdsSubQuery = taskIdSubQueryForReport(params, query, cb);
				if (taskIdsSubQuery != null) {
					Predicate taskIdsPredicate = root.get("taskId").as(Long.class).in(taskIdsSubQuery);
					predicates.add(taskIdsPredicate);
				}

				/*
				 * Subquery<Long> taskItemIdsSubQuery =
				 * taskItemIdSubQueryForReport(params, query, cb); if
				 * (taskItemIdsSubQuery != null) { Predicate
				 * taskItemIdsPredicate =
				 * root.get("id").as(Long.class).in(taskItemIdsSubQuery);
				 * predicates.add(taskItemIdsPredicate); }
				 */

				query = query.where(toPredicateArray(predicates));
				return query.getRestriction();
			}
		};
	}

	private static Predicate[] toPredicateArray(List<Predicate> list) {
		Predicate[] array = new Predicate[list.size()];
		list.toArray(array);
		return array;
	}

	@Override
	public void updateAssetTags(String assetNo, Long roadTagId, Long weatherTagId) {
		this.taskRepo.updateTags(assetNo, roadTagId, weatherTagId);
		this.taskItemRepo.updateTags(assetNo, roadTagId, weatherTagId);
	}

	@Override
	public void updateAssetTags(String taskItemNo, AssetTagParam tagParam) {
		TaskItem taskItem = this.taskItemRepo.find(taskItemNo);
		if (taskItem != null) {
			taskItem.setRoadTagId(tagParam.getRoadTagId());
			taskItem.setWeatherTagId(tagParam.getWeatherTagId());
			this.taskItemRepo.save(taskItem);
		}
	}

	@Autowired
	private TaskItemXViewTagRepository taskItemXViewTagRepo;

	@Transactional
	@Override
	public void updateViewTags(String taskItemNo, ViewTagParam tagParam) {
		TaskItem taskItem = this.taskItemRepo.find(taskItemNo);
		if (taskItem != null) {
			taskItemXViewTagRepo.deleteTags(taskItem.getId());
			for (TaskItemXViewTag tag : tagParam.getItems()) {
				tag.setTaskItemId(taskItem.getId());
				taskItemXViewTagRepo.save(tag);
			}
		}
	}

	@Override
	public void deleteTaskByAsset(Asset asset) {
		List<Task> tasks = this.taskRepo.findTasks(asset.getAssetNo());
		for (Task task : tasks) {
			this.deleteTask(task);
		}
		// this.taskRepo.deleteTaskByAssetNo(asset.getAssetNo());
	}

	private void deleteTask(Task task) {
		this.taskXViewTagRespo.deleteTags(task.getId());
		List<TaskItem> taskItems = this.taskItemRepo.getTaskItems(task.getId());
		for (TaskItem taskItem : taskItems) {
			this.deleteTaskItem(taskItem);
		}
		this.taskRepo.delete(task.getId());
	}

	private void deleteTaskItem(TaskItem taskItem) {
		this.taskItemFileRepo.deleteTaskItemFiles(taskItem.getTaskItemNo());
		this.taskItemXViewTagRepo.deleteTags(taskItem.getId());
		this.taskRecordService.deleteTaskRecord(taskItem);
		this.taskItemRepo.delete(taskItem.getId());
	}

	@Transactional
	@Override
	public void deleteTask(String taskNo) throws BusinessException {
		Task task = this.getTaskByTaskNo(taskNo);
		this.deleteTask(task);
	}

	@Override
	public Long countTaskItems(Long taskId) {
		return this.taskItemRepo.countTaskItems(taskId);
	}

	public TaskItem getSimpleTaskItem(String taskItemNo) {
		return this.taskItemRepo.find(taskItemNo);
	}

	@Override
	public void newTask(String taskItemNo) {
		TaskItem taskItem = this.taskItemRepo.find(taskItemNo);
		taskItem.setStatus(TaskItemStatus.NEW);
		this.taskItemRepo.save(taskItem);

	}

}

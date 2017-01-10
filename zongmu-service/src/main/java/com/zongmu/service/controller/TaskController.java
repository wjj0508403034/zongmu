package com.zongmu.service.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zongmu.service.criteria.Equal;
import com.zongmu.service.criteria.Filter;
import com.zongmu.service.criteria.GreaterThan;
import com.zongmu.service.criteria.LessThan;
import com.zongmu.service.criteria.Like;
import com.zongmu.service.criteria.QueryParams;
import com.zongmu.service.criteria.TaskContainsRoadTags;
import com.zongmu.service.criteria.TaskContainsWeatherTag;
import com.zongmu.service.dto.asset.AssetObject;
import com.zongmu.service.dto.asset.AssetTagParam;
import com.zongmu.service.dto.asset.ViewTagParam;
import com.zongmu.service.dto.search.HomePageSearchParam;
import com.zongmu.service.dto.task.TaskItemStatus;
import com.zongmu.service.dto.task.TaskObject;
import com.zongmu.service.dto.task.TaskType;
import com.zongmu.service.dto.taskrecord.TaskRecordObject;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskRecord;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.ffmpeg.CutFailure;
import com.zongmu.service.ffmpeg.CutResult;
import com.zongmu.service.ffmpeg.CutSuccess;
import com.zongmu.service.ffmpeg.VideoItemInfo;
import com.zongmu.service.ffmpeg.cut.TaskInfo;
import com.zongmu.service.internal.service.ReviewRecordService;
import com.zongmu.service.internal.service.TaskRecordService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.util.MapperService;

@Controller
public class TaskController {

	private static Logger logger = Logger.getLogger(TaskController.class);

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskRecordService taskRecordService;

	@Autowired
	private ReviewRecordService reviewRecordService;

	@Autowired
	private MapperService mapperService;

	@RequestMapping(value = "/tasks", method = RequestMethod.POST)
	@ResponseBody
	public Task createTask(@RequestBody TaskObject taskObject) throws BusinessException {
		Task task = this.mapperService.map(taskObject, Task.class);
		return this.taskService.createTask(task);
	}

	@RequestMapping(value = "/tasks/{taskNo}", method = RequestMethod.GET)
	@ResponseBody
	public Task getTaskDetail(@PathVariable("taskNo") String taskNo,
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize)
					throws BusinessException {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.taskService.getTaskDetail(taskNo, pageable);
	}
	
	@RequestMapping(value = "/tasks/{taskNo}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteTask(@PathVariable("taskNo") String taskNo)
					throws BusinessException {
		 this.taskService.deleteTask(taskNo);
	}


	@RequestMapping(value = "/tasks/{taskNo}/updateTags", method = RequestMethod.POST)
	@ResponseBody
	public void setAssetTags(@PathVariable("taskNo") String taskNo, @RequestBody AssetObject assetObject)
			throws BusinessException {
		this.taskService.setAssetTags(taskNo, assetObject.getTagIds());
	}
	
	@RequestMapping(value = "/tasks/queryTasks", method = RequestMethod.POST)
	@ResponseBody
	public Page<TaskItem> queryTasks(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestBody HomePageSearchParam homePageSearchParam) {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.taskService.queryTasks(homePageSearchParam, pageable);
	}
	
	@RequestMapping(value = "/tasks", method = RequestMethod.GET)
	@ResponseBody
	public Page<TaskItem> getTasks(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestParam(value = "filter", required = false) String filter) {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		QueryParams params = new QueryParams();
		if (!StringUtils.isEmpty(filter)) {
			String[] wheres = filter.split(";");
			for (String where : wheres) {
				if (where.contains(" like ")) {
					params.add(Like.parse(where));
				} else if (where.contains(" eq ") || where.contains(" = ")) {
					Filter equal = this.parseToEqual(where);
					if (equal != null) {
						params.add(equal);
					}
				} else if (where.contains(" > ")) {
					GreaterThan gt = this.parseToGreaterThan(where);
					if (gt != null) {
						params.add(gt);
					}
				} else if (where.contains(" < ")) {
					LessThan lt = this.parseToLessThan(where);
					if (lt != null) {
						params.add(lt);
					}
				} else if (where.contains(" in ")) {
					String[] parts = where.split(" ");
					if (StringUtils.equalsIgnoreCase("roadTagIds", parts[0])) {
						params.add(new TaskContainsRoadTags(parts[2]));
					} else if (StringUtils.equalsIgnoreCase("weatherTagIds", parts[0])) {
						params.add(new TaskContainsWeatherTag(parts[2]));
					}
				}
			}
		} else {
			params.add(new Equal("status", TaskItemStatus.NEW, TaskItemStatus.class));
		}

		return this.taskService.getTaskItems(params, pageable);
	}

	private Filter parseToEqual(String expr) {
		String[] parts = expr.split(" ");
		if (StringUtils.equalsIgnoreCase(parts[0], "point")) {
			return new Equal("point", Integer.parseInt(parts[2]), int.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "taskType")) {
			return new Equal("taskType", TaskType.valueOf(parts[2]), TaskType.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "status")) {
			return new Equal("status", TaskItemStatus.valueOf(parts[2]), TaskItemStatus.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "createTimeFrom")) {
			DateTime from = this.parse(parts[2]);
			if (from != null) {
				return new GreaterThan("createTime", from, DateTime.class);
			}

		} else if (StringUtils.equalsIgnoreCase(parts[0], "createTimeTo")) {

			DateTime to = this.parse(parts[2]);
			if (to != null) {
				return new GreaterThan("createTime", to, DateTime.class);
			}
		}

		return null;
	}

	private DateTime parse(String val) {
		try {
			return DateTime.parse(val);
		} catch (Exception ex) {
			logger.error("Parse value " + val + " to date failed");
			return null;
		}

	}

	private GreaterThan parseToGreaterThan(String expr) {
		String[] parts = expr.split(" ");
		if (StringUtils.equalsIgnoreCase(parts[0], "point")) {
			return new GreaterThan("point", Integer.parseInt(parts[2]), int.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "createTime")) {
			return new GreaterThan("createTime", DateTime.parse(parts[2]), DateTime.class);
		}

		return null;
	}

	private LessThan parseToLessThan(String expr) {
		String[] parts = expr.split(" ");
		if (StringUtils.equalsIgnoreCase(parts[0], "point")) {
			return new LessThan("point", Integer.parseInt(parts[2]), int.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "createTime")) {
			return new LessThan("createTime", DateTime.parse(parts[2]), DateTime.class);
		}

		return null;
	}

	@RequestMapping(value = "/tasks/my", method = RequestMethod.GET)
	@ResponseBody
	public Page<TaskItem> getMyTasks(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.taskService.getTaskItems(null, pageable);
	}

	@RequestMapping(value = "tasks/{taskItemNo}/updateTaskTags", method = RequestMethod.POST)
	@ResponseBody
	public void updateAssetTags(@PathVariable("taskItemNo") String taskItemNo, @RequestBody AssetTagParam tagParam)
			throws BusinessException {
		this.taskService.updateAssetTags(taskItemNo, tagParam);
	}
	
	@RequestMapping(value = "tasks/{taskItemNo}/updateViewTags", method = RequestMethod.POST)
	@ResponseBody
	public void updateViewTags(@PathVariable("taskItemNo") String taskItemNo, @RequestBody ViewTagParam tagParam)
			throws BusinessException {
		this.taskService.updateViewTags(taskItemNo, tagParam);
	}

	@RequestMapping(value = "tasks/{taskItemNo}/accept", method = RequestMethod.POST)
	@ResponseBody
	public TaskItem acceptTask(@PathVariable("taskItemNo") String taskItemNo) throws BusinessException {
		return this.taskService.acceptTask(taskItemNo);
	}

	@RequestMapping(value = "tasks/batchAccept", method = RequestMethod.POST)
	@ResponseBody
	public void acceptTask(@RequestBody List<String> taskItemNos) throws BusinessException {
		this.taskService.batchAcceptTask(taskItemNos);
	}

	@RequestMapping("/tasks/{taskItemNo}/detail")
	@ResponseBody
	public TaskItem getTask(@PathVariable("taskItemNo") String taskItemNo) throws BusinessException {
		TaskItem task = this.taskService.getTaskItem(taskItemNo);
		List<TaskRecord> taskRecords = this.taskRecordService.getTaskRecords(taskItemNo);
		task.setTaskRecords(this.mapperService.mapList(taskRecords, TaskRecordObject.class));
		task.setReviewRecords(this.reviewRecordService.getReviewRecordsByTaskItemNo(taskItemNo));
		return task;
	}

	@RequestMapping(value = "/getTasksByAssetNo", method = RequestMethod.GET)
	@ResponseBody
	public List<TaskObject> getTasksByAssetNo(@RequestParam(value = "assetNo") String assetNo) {
		List<Task> tasks = this.taskService.getTasksByAssetNo(assetNo);
		return this.mapperService.mapList(tasks, TaskObject.class);
	}

	@RequestMapping(value = "/tasks/{taskNo}/cutCallback", method = RequestMethod.POST)
	@ResponseBody
	public void cutCallback(@PathVariable("taskNo") String taskNo, @RequestBody TaskInfo taskInfo)
			throws BusinessException {
		this.taskService.batchCreateTaskItem(taskInfo);
	}

	@RequestMapping(value = "/tasks/cutFailure", method = RequestMethod.POST)
	@ResponseBody
	public void videoCutFailed(@RequestBody CutFailure cutFailure) throws BusinessException {
		this.taskService.cutFailure(cutFailure);
	}

	@RequestMapping(value = "/tasks/cutSuccess", method = RequestMethod.POST)
	@ResponseBody
	public void videoCutSuccess(@RequestBody CutSuccess cutSuccess) throws BusinessException {
		this.taskService.cutSuccess(cutSuccess);
	}

	@RequestMapping(value = "/tasks/cutItemFailure", method = RequestMethod.POST)
	@ResponseBody
	public void videoItemCutFailed(@RequestBody VideoItemInfo videoItemInfo) throws BusinessException {
		this.taskService.cutItemFailure(videoItemInfo);
	}

	@RequestMapping(value = "/tasks/cutItemSuccess", method = RequestMethod.POST)
	@ResponseBody
	public void videoItemCutSuccess(@RequestBody VideoItemInfo videoItemInfo) throws BusinessException {
		this.taskService.cutItemSuccess(videoItemInfo);
	}

	@RequestMapping(value = "/tasks/{taskNo}/top", method = RequestMethod.POST)
	@ResponseBody
	public void setTop(@PathVariable("taskNo") String taskNo, @RequestBody boolean top) throws BusinessException {
		this.taskService.setTop(taskNo, top);
	}

	@RequestMapping(value = "/tasks/{taskNo}/priority", method = RequestMethod.POST)
	@ResponseBody
	public void setPriority(@PathVariable("taskNo") String taskNo, @RequestBody int priority) throws BusinessException {
		this.taskService.setPriority(taskNo, priority);
	}

	@RequestMapping(value = "/tasks/{taskNo}/show", method = RequestMethod.POST)
	@ResponseBody
	public void setShow(@PathVariable("taskNo") String taskNo, @RequestBody boolean show) throws BusinessException {
		this.taskService.setShow(taskNo, show);
	}

	@RequestMapping(value = "/tasks/cutFinished", method = RequestMethod.POST)
	@ResponseBody
	public void cutFinished(@RequestBody CutResult cutResult) throws BusinessException {
		this.taskService.cutFinished(cutResult);
	}
}

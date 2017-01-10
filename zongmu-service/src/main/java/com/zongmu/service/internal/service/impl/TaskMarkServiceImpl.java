package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zongmu.service.entity.MarkShape;
import com.zongmu.service.entity.MarkTag;
import com.zongmu.service.entity.Point;
import com.zongmu.service.entity.TaskItemFileMark;
import com.zongmu.service.entity.TaskMark;
import com.zongmu.service.entity.Timeline;
import com.zongmu.service.internal.service.TaskMarkService;
import com.zongmu.service.repository.MarkShapeRepository;
import com.zongmu.service.repository.MarkTagRepository;
import com.zongmu.service.repository.PointRepository;
import com.zongmu.service.repository.TaskItemFileMarkRepository;
import com.zongmu.service.repository.TaskMarkRepository;
import com.zongmu.service.repository.TimelineRepository;

@Service
public class TaskMarkServiceImpl implements TaskMarkService {

	@Autowired
	private TaskMarkRepository taskMarkRepo;

	@Autowired
	private MarkShapeRepository markShapeRepo;

	@Autowired
	private TimelineRepository timelineRepo;

	@Autowired
	private MarkTagRepository markTagRepo;

	@Autowired
	private PointRepository pointRepo;

	@Autowired
	private TaskItemFileMarkRepository taskItemFileMarkRepo;

	@Override
	public TaskMark createTaskMark(Long taskRecordId) {
		TaskMark taskMark = new TaskMark();
		taskMark.setTaskRecordId(taskRecordId);
		this.taskMarkRepo.save(taskMark);
		return taskMark;
	}

	@Override
	public TaskMark getTaskMark(Long taskRecordId) {
		TaskMark taskMark = null;
		taskMark = this.taskMarkRepo.getTaskMark(taskRecordId);
		if (taskMark != null) {
			//taskMark.setMarkShapes(this.getMarkShapes(taskMark));
			taskMark.setTaskItemFileMarks(this.getTaskItemFileMarks(taskMark));
		}
		return taskMark;
	}

	@Override
	public void saveTaskMarks(Long taskRecordId, TaskMark taskMark) {
		this.deleteTaskMarks(taskRecordId);
		taskMark.setTaskRecordId(taskRecordId);
		this.saveTaskMark(taskMark);
	}

	@Override
	public void deleteTaskMarks(Long taskRecordId) {
		TaskMark taskMark = this.taskMarkRepo.getTaskMark(taskRecordId);
		this.deleteTaskItemFileMarks(taskMark);
		this.taskMarkRepo.deleteTaskMarks(taskRecordId);
	}

	private void deleteTaskItemFileMarks(TaskMark taskMark) {
		if (taskMark != null) {
			List<TaskItemFileMark> marks = this.taskItemFileMarkRepo.getTaskItemFileMarks(taskMark.getId());
			for (TaskItemFileMark mark : marks) {
				this.deleteMarkShapes(mark);
			}

			this.taskItemFileMarkRepo.deleteTaskItemFileMarks(taskMark.getId());
		}
	}

	private void deleteMarkShapes(TaskItemFileMark taskItemFileMark) {
		if (taskItemFileMark != null) {
			List<MarkShape> markShapes = this.markShapeRepo.getMarkShapes(taskItemFileMark.getId());
			for (MarkShape markShape : markShapes) {
				this.deleteTimelines(markShape);
				this.deleteMarkTags(markShape);
			}
			this.markShapeRepo.deleteMarkShapes(taskItemFileMark.getId());
		}
	}

	private void deleteMarkTags(MarkShape markShape) {
		this.markTagRepo.deleteTags(markShape.getId());
	}

	private void deleteTimelines(MarkShape markShape) {
		List<Timeline> timelines = this.timelineRepo.getTimelines(markShape.getId());
		for (Timeline timeline : timelines) {
			this.pointRepo.deletePoints(timeline.getId());
		}

		this.timelineRepo.deleteTimelines(markShape.getId());
	}

	private void saveTaskMark(TaskMark taskMark) {
		TaskMark newTaskMark = this.taskMarkRepo.save(taskMark);
		/*
		 * for (MarkShape markShape : taskMark.getMarkShapes()) {
		 * markShape.setTaskMarkId(newTaskMark.getId());
		 * this.saveMarkShape(markShape); }
		 */

		for (TaskItemFileMark taskItemFileMark : taskMark.getTaskItemFileMarks()) {
			taskItemFileMark.setTaskMarkId(newTaskMark.getId());
			this.saveTaskItemFileMark(taskItemFileMark);
		}
	}

	private void saveTaskItemFileMark(TaskItemFileMark taskItemFileMark) {
		TaskItemFileMark newTaskItemFileMark = this.taskItemFileMarkRepo.save(taskItemFileMark);
		for (MarkShape markShape : taskItemFileMark.getMarkShapes()) {
			markShape.setTaskMarkId(newTaskItemFileMark.getTaskMarkId());
			markShape.setTaskItemFileMarkId(newTaskItemFileMark.getId());
			this.saveMarkShape(markShape);
		}
	}

	private void saveMarkShape(MarkShape markShape) {
		MarkShape newMarkShape = this.markShapeRepo.save(markShape);
		for (Timeline timeline : markShape.getTimelines()) {
			timeline.setMarkShapeId(newMarkShape.getId());
			this.saveTimeline(timeline);
		}

		for (MarkTag markTag : markShape.getTags()) {
			markTag.setMarkShapeId(newMarkShape.getId());
			this.markTagRepo.save(markTag);
		}
	}

	private void saveTimeline(Timeline timeline) {
		Timeline newTimeline = this.timelineRepo.save(timeline);
		for (Point point : timeline.getPoints()) {
			point.setTimelineId(newTimeline.getId());
			this.pointRepo.save(point);
		}
	}

	private List<MarkShape> getMarkShapes(TaskItemFileMark taskItemFileMark) {
		List<MarkShape> markShapes = this.markShapeRepo.getMarkShapes(taskItemFileMark.getId());
		for (MarkShape markShape : markShapes) {
			markShape.setTimelines(this.getTimelines(markShape));
			markShape.setTags(this.markTagRepo.getTags(markShape.getId()));
		}
		return markShapes;
	}

	private List<TaskItemFileMark> getTaskItemFileMarks(TaskMark taskMark) {
		List<TaskItemFileMark> taskItemFileMarks = this.taskItemFileMarkRepo.getTaskItemFileMarks(taskMark.getId());
		for (TaskItemFileMark taskItemFileMark : taskItemFileMarks) {
			taskItemFileMark.setMarkShapes(this.getMarkShapes(taskItemFileMark));
		}
		return taskItemFileMarks;
	}

	private List<Timeline> getTimelines(MarkShape markShape) {
		List<Timeline> timelines = this.timelineRepo.getTimelines(markShape.getId());
		for (Timeline timeline : timelines) {
			timeline.setPoints(this.pointRepo.getPoints(timeline.getId()));
		}
		return timelines;
	}

}

package com.zongmu.service.internal.service.mark.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.mark.ShapeFrameIndexInfo;
import com.zongmu.service.entity.mark.TaskMarkGroup;
import com.zongmu.service.entity.mark.TaskMarkRecord;
import com.zongmu.service.entity.mark.TaskMarkRecordRefTag;
import com.zongmu.service.internal.service.ColorTagService;
import com.zongmu.service.internal.service.mark.TaskMarkGroupService;
import com.zongmu.service.internal.service.mark.TaskMarkRecordRefTagService;
import com.zongmu.service.internal.service.mark.TaskMarkRecordService;
import com.zongmu.service.repository.mark.ShapeFrameIndexInfoRepository;
import com.zongmu.service.repository.mark.TaskMarkRecordRepository;

@Service
public class TaskMarkRecordServiceImpl implements TaskMarkRecordService {

	@Autowired
	private TaskMarkRecordRepository taskMarkRecordRepo;

	@Autowired
	private TaskMarkRecordRefTagService recordRefTagService;

	@Autowired
	private ColorTagService colorTagService;

	@Autowired
	private TaskMarkGroupService taskMarkGroupService;

	@Override
	public List<TaskMarkRecord> getRecords(Long taskRecordId) {
		List<TaskMarkRecord> records = this.taskMarkRecordRepo.getRecords(taskRecordId);
		for (TaskMarkRecord record : records) {
			record.setTags(this.recordRefTagService.getRefTags(record.getId()));
			record.setColorTag(this.colorTagService.getTag(record.getColorTagId()));
			record.setGroups(this.taskMarkGroupService.getGroups(record.getId()));
			record.setShapeFrameIndexInfos(this.getShapeFrameIndexInfos(record.getId()));
		}

		return records;
	}

	private List<ShapeFrameIndexInfo> getShapeFrameIndexInfos(Long id) {
		return this.shapeFrameIndexInfoRepo.query(id);
	}

	@Transactional
	@Override
	public void saveRecord(Long taskRecordId, List<TaskMarkRecord> taskMarkRecords) {
		this.deleteRecords(taskRecordId);
		for (TaskMarkRecord taskMarkRecord : taskMarkRecords) {
			taskMarkRecord.setTaskRecordId(taskRecordId);
			this.saveRecord(taskMarkRecord);
		}
	}

	@Autowired
	private ShapeFrameIndexInfoRepository shapeFrameIndexInfoRepo;

	private void saveRecord(TaskMarkRecord taskMarkRecord) {
		TaskMarkRecord newRecord = this.taskMarkRecordRepo.save(taskMarkRecord);
		
		Map<Long,TaskMarkRecordRefTag> map = new HashMap<>();
		for (TaskMarkRecordRefTag refTag : taskMarkRecord.getTags()) {
			if(!map.containsKey(refTag.getTagItemId())){
				map.put(refTag.getTagItemId(), refTag);
			}
		}
		for (TaskMarkRecordRefTag refTag : map.values()) {
			refTag.setTaskMarkRecordId(newRecord.getId());
			this.recordRefTagService.save(refTag);
		}

		for (TaskMarkGroup group : taskMarkRecord.getGroups()) {
			group.setTaskMarkRecordId(newRecord.getId());
			this.taskMarkGroupService.save(group);
		}

		for (ShapeFrameIndexInfo info : taskMarkRecord.getShapeFrameIndexInfos()) {
			info.setTaskMarkRecordId(newRecord.getId());
			this.shapeFrameIndexInfoRepo.save(info);
		}

	}

	@Transactional
	@Override
	public void deleteRecords(Long taskRecordId) {
		List<TaskMarkRecord> records = this.taskMarkRecordRepo.getRecords(taskRecordId);
		for (TaskMarkRecord record : records) {
			this.recordRefTagService.deleteRefTags(record.getId());
			// this.taskMarkShapeService.deleteShapes(record.getId());
			this.taskMarkGroupService.deleteGroup(record.getId());
		}
		this.taskMarkRecordRepo.deleteRecords(taskRecordId);
	}

	@Override
	public Long countByColorTag(Long colorTagId) {
		return this.taskMarkRecordRepo.countByColorTag(colorTagId);
	}

}

package com.zongmu.service.internal.service.mark.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.mark.TaskMarkRecordRefTag;
import com.zongmu.service.internal.service.mark.TaskMarkRecordRefTagService;
import com.zongmu.service.repository.mark.TaskMarkRecordRefTagRepository;

@Service
public class TaskMarkRecordRefTagServiceImpl implements TaskMarkRecordRefTagService {

	@Autowired
	private TaskMarkRecordRefTagRepository taskMarkRecordRefTagRepo;

	@Override
	public List<TaskMarkRecordRefTag> getRefTags(Long taskMarkRecordId) {
		List<TaskMarkRecordRefTag> refTags = this.taskMarkRecordRefTagRepo.getRefTags(taskMarkRecordId);
//		for (TaskMarkRecordRefTag refTag : refTags) {
//			refTag.setTagItem(this.tagService.getTagItem(refTag.getTagItemId()));
//		}
		return refTags;
	}

	@Transactional
	@Override
	public void deleteRefTags(Long taskMarkRecordId) {
		this.taskMarkRecordRefTagRepo.deleteRefTags(taskMarkRecordId);
	}

	@Transactional
	@Override
	public void save(TaskMarkRecordRefTag refTag) {
		this.taskMarkRecordRefTagRepo.save(refTag);
	}

	@Override
	public Long countByTagItem(Long tagItemId) {
		return this.taskMarkRecordRefTagRepo.countByTagItem(tagItemId);
	}

}

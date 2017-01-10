package com.zongmu.service.internal.service.mark;

import java.util.List;

import com.zongmu.service.entity.mark.TaskMarkRecordRefTag;

public interface TaskMarkRecordRefTagService {

	List<TaskMarkRecordRefTag> getRefTags(Long taskMarkRecordId);
	
	void deleteRefTags(Long taskMarkRecordId);
	
	void save(TaskMarkRecordRefTag refTag);
	
	Long countByTagItem(Long tagItemId);
}

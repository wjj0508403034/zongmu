package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.zongmu.service.entity.TaskItemXViewTag;

public interface TaskItemXViewTagRepository extends CrudRepository<TaskItemXViewTag, Long> {

	@Modifying
	@Query("delete from TaskItemXViewTag t where t.taskItemId = ?1")
	void deleteTags(Long taskItemId);

	@Query("select t from TaskItemXViewTag t where t.taskItemId = ?1")
	List<TaskItemXViewTag> getTags(Long taskItemId);
	
	@Query("select count(t) > 0 from TaskItemXViewTag t where t.viewTagId = ?1")
	boolean isViewTagUsing(Long viewTagId);
	
	@Query("select count(t) > 0 from TaskItemXViewTag t where t.viewTagItemId = ?1")
	boolean isViewTagItemUsing(Long viewTagItemId);
}

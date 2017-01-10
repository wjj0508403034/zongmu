package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.TaskXViewTag;

@Repository
public interface TaskXViewTagRepository extends PagingAndSortingRepository<TaskXViewTag, Long> {

	@Query("select count(t) > 0 from TaskXViewTag t where t.viewTagId = ?1")
	boolean isViewTagUsing(Long viewTagId);
	
	@Query("select count(t) > 0 from TaskXViewTag t where t.viewTagItemId = ?1")
	boolean isViewTagItemUsing(Long viewTagItemId);
	
	@Query("select t from TaskXViewTag t where t.taskId = ?1")
	List<TaskXViewTag> getListByTaskId(Long taskId);
	
	@Modifying
	@Query("delete from TaskXViewTag t where t.taskId = ?1")
	void deleteTags(Long taskId);
}

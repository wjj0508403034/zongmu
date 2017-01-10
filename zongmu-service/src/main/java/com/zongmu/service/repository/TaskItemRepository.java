package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.TaskItem;

@Repository
public interface TaskItemRepository extends PagingAndSortingRepository<TaskItem, Long> ,JpaSpecificationExecutor<TaskItem> {

	@Query("select t from TaskItem t where t.taskItemNo = ?1")
	TaskItem find(String taskItemNo);

	@Query("select t from TaskItem t where t.taskId = ?1")
	List<TaskItem> getTaskItems(Long taskId);

	@Query("select t from TaskItem t order by t.top desc, t.priority")
	Page<TaskItem> getTaskItems(Pageable pageable);

	@Query("select t from TaskItem t where t.taskId = ?1")
	Page<TaskItem> getTaskItems(Long taskId, Pageable pageable);
	
	@Modifying
	@Query("update TaskItem t set t.roadTagId = ?2 , t.weatherTagId = ?3 where t.assetNo = ?1")
	void updateTags(String assetNo, Long roadTagId, Long weatherTagId);
	
	@Modifying
	@Query("delete from TaskItem t where t.taskId = ?1")
	void deleteTaskItemsByTaskId(Long taskId);
	
	@Query("select count(t) from TaskItem t where t.taskId = ?1")
	Long countTaskItems(Long taskId);

}

package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.TaskItemFile;

@Repository
public interface TaskItemFileRepository extends PagingAndSortingRepository<TaskItemFile, Long> {

	@Query("select t from TaskItemFile t where t.taskItemNo = ?1")
	List<TaskItemFile> getTaskItemFiles(String taskItemNo);

	@Query("select count(t) from TaskItemFile t where t.taskItemNo = ?1 and t.status = 0")
	int countSuccessFiles(String taskItemNo);

	@Modifying
	@Query("delete from TaskItemFile t where t.taskItemNo = ?1")
	void deleteTaskItemFiles(String taskItemNo);
}

package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.TaskItemFileMark;

@Repository
public interface TaskItemFileMarkRepository  extends PagingAndSortingRepository<TaskItemFileMark, Long>{

	@Query("select t from TaskItemFileMark t where t.taskMarkId = ?1")
	List<TaskItemFileMark> getTaskItemFileMarks(Long taskMarkId);
	
    @Modifying
    @Query("delete from TaskItemFileMark t where t.taskMarkId = ?1")
    void deleteTaskItemFileMarks(Long taskMarkId);
}

package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.Task2AssetViewTag;

@Repository
public interface Task2AssetViewTagRepository extends PagingAndSortingRepository<Task2AssetViewTag, Long> {

	@Query("select t from Task2AssetViewTag t where t.taskId = ?1")
	List<Task2AssetViewTag> getListByTaskId(Long taskId);

	@Modifying
	@Query("delete from Task2AssetViewTag t where t.taskId = ?1")
	void deleteTags(Long taskId);
}

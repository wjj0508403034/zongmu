package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.TaskRefAssetTag;

@Repository
public interface TaskRefAssetTagRepository extends PagingAndSortingRepository<TaskRefAssetTag, Long> {

	@Query("select t from TaskRefAssetTag t where t.taskId = ?1")
	List<TaskRefAssetTag> getAssetRefAssetTags(Long taskId);

	@Query("select count(t) from TaskRefAssetTag t where t.assetTagId = ?1")
	Long countByAssetTag(Long assetTagId);

	@Modifying
	@Query("delete from TaskRefAssetTag t where t.taskId = ?1")
	void deleteTags(Long taskId);
}

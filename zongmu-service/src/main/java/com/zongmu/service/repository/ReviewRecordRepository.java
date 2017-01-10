package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.dto.reviewrecord.ReviewRecordStatus;
import com.zongmu.service.entity.ReviewRecord;

@Repository
public interface ReviewRecordRepository
		extends PagingAndSortingRepository<ReviewRecord, Long>, JpaSpecificationExecutor<ReviewRecord> {

	@Query("select t from ReviewRecord t where t.taskItemNo = ?1 order by t.id desc")
	List<ReviewRecord> getReviewRecordsByTaskItemNo(String taskItemNo);

	@Query("select t from ReviewRecord t where t.taskRecordNo = ?1 and t.status = ?2")
	ReviewRecord getReviewRecord(String taskRecordNo, ReviewRecordStatus status);

	@Query("select t from ReviewRecord t where t.reviewRecordNo = ?1")
	ReviewRecord getReviewRecord(String reviewRecordNo);

	@Query("select count(t) from ReviewRecord t where t.reasonId = ?1")
	Long countByReason(Long reasonId);

	@Query("select t from ReviewRecord t where t.status = ?1")
	Page<ReviewRecord> getList(ReviewRecordStatus status, Pageable pageable);

	@Modifying
	@Query("delete from ReviewRecord t where t.taskItemNo = ?1")
	void deleteRecords(String taskItemNo);

	@Query("select count(t) from ReviewRecord t where t.status = 2 and t.taskItemNo in "
			+ "(select taskItem.taskItemNo from TaskItem taskItem where taskItem.taskId = ?1)")
	Long finishTaskCount(Long taskId);
}

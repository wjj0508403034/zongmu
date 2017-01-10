package com.zongmu.service.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.dto.taskrecord.TaskRecordStatus;
import com.zongmu.service.entity.TaskRecord;

@Repository
public interface TaskRecordRepository extends PagingAndSortingRepository<TaskRecord, Long>, JpaSpecificationExecutor<TaskRecord> {

	@Query("select t from TaskRecord t where t.taskRecordNo = ?1")
	TaskRecord getTaskRecord(String taskRecordNo);
	
	@Modifying
	@Query("delete from TaskRecord t where t.taskItemNo = ?1")
	void deleteTaskRecords(String taskItemNo);

	@Query("select t from TaskRecord t where t.taskItemNo = ?1 order by t.id desc")
	List<TaskRecord> getTaskRecords(String taskItemNo);

	@Query("select t from TaskRecord t where t.startTime < ?1 and t.status = ?2")
	List<TaskRecord> getTimeoutRecords(DateTime startTime, TaskRecordStatus status);

	@Query("select t from TaskRecord t where t.userId = ?1")
	Page<TaskRecord> getMyTaskRecords(Long userId, Pageable pageable);

	@Query("select t from TaskRecord t where t.userId = ?1 and t.status = ?2")
	Page<TaskRecord> getMyTaskRecords(Long userId, TaskRecordStatus status, Pageable pageable);

	@Query("select t from TaskRecord t where t.status = ?1")
	Page<TaskRecord> getAdminTaskRecords(TaskRecordStatus status, Pageable pageable);

	@Query("select t from TaskRecord t")
	Page<TaskRecord> getAdminTaskRecords(Pageable pageable);

	@Query("select count(t) from TaskRecord t where t.userId = ?1 and t.status = 4")
	Long countRejectRecords(Long userId);

	@Query("select t from TaskRecord t where t.id = (select max(tt.id) from TaskRecord tt where tt.id < ?1 and tt.userId = ?2)")
	TaskRecord getPervious(Long id, Long userId);

	@Query("select t from TaskRecord t where t.id = (select min(tt.id) from TaskRecord tt where tt.id > ?1 and tt.userId = ?2)")
	TaskRecord getNext(Long id, Long userId);

	@Query("select t from TaskRecord t where t.id = (select max(tt.id) from TaskRecord tt where tt.id < ?1)")
	TaskRecord getAdminPervious(Long id);

	@Query("select t from TaskRecord t where t.id = (select min(tt.id) from TaskRecord tt where tt.id > ?1)")
	TaskRecord getAdminNext(Long id);

	@Query("select t from TaskRecord t where t.id = (select max(tt.id) from TaskRecord tt where tt.id < ?1 and "
			+ "tt.taskRecordNo in (select review.taskRecordNo from ReviewRecord review where review.userId = ?2))")
	TaskRecord getReviewPervious(Long id, Long userId);

	@Query("select t from TaskRecord t where t.id = (select min(tt.id) from TaskRecord tt where tt.id > ?1 and"
			+ " tt.taskRecordNo in (select review.taskRecordNo from ReviewRecord review where review.userId = ?2))")
	TaskRecord getReviewNext(Long id, Long userId);

	@Query("select t from TaskRecord t where t.id = (select max(tt.id) from TaskRecord tt where tt.id < ?1 and tt.reviewRecordNo <> null)")
	TaskRecord getAdminReviewPervious(Long id);

	@Query("select t from TaskRecord t where t.id = (select min(tt.id) from TaskRecord tt where tt.id > ?1 and tt.reviewRecordNo <> null)")
	TaskRecord getAdminReviewNext(Long id);
}

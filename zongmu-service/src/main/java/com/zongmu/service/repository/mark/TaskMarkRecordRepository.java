package com.zongmu.service.repository.mark;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.mark.TaskMarkRecord;

@Repository
public interface TaskMarkRecordRepository extends CrudRepository<TaskMarkRecord, Long> {

	@Query("select t from TaskMarkRecord t where t.taskRecordId = ?1")
	List<TaskMarkRecord> getRecords(Long taskRecordId);

	@Query("select t from TaskMarkRecord t where t.id = ?1")
	List<TaskMarkRecord> getRecordsById(Long id);

	@Transactional
	@Modifying
	@Query("delete from TaskMarkRecord t where t.taskRecordId = ?1")
	void deleteRecords(Long taskRecordId);

	@Query("select count(t) from TaskMarkRecord t where t.colorTagId = ?1")
	Long countByColorTag(Long colorTagId);
}

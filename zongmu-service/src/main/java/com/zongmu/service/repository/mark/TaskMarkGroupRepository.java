package com.zongmu.service.repository.mark;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.mark.TaskMarkGroup;

@Repository
public interface TaskMarkGroupRepository extends CrudRepository<TaskMarkGroup, Long>{

	@Query("select t from TaskMarkGroup t where t.taskMarkRecordId = ?1")
	List<TaskMarkGroup> getShapes(Long taskMarkRecordId);

	@Transactional
	@Modifying
	@Query("delete from TaskMarkGroup t where t.taskMarkRecordId = ?1")
	void deleteShapes(Long taskMarkRecordId);
}

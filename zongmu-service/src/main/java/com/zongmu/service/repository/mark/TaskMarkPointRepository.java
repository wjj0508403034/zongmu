package com.zongmu.service.repository.mark;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.mark.TaskMarkPoint;

@Repository
public interface TaskMarkPointRepository extends CrudRepository<TaskMarkPoint, Long> {

	@Query("select t from TaskMarkPoint t where t.taskMarkGroupId = ?1")
	List<TaskMarkPoint> getShapes(Long taskMarkGroupId);

	@Transactional
	@Modifying
	@Query("delete from TaskMarkPoint t where t.taskMarkGroupId = ?1")
	void deletePoints(Long taskMarkGroupId);
}

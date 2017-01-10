package com.zongmu.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.UserPoint;

@Repository
public interface UserPointRepository extends CrudRepository<UserPoint, Long> {

	@Query("select sum(t.point) from UserPoint t where t.userId = ?1")
	Long sumMyPoint(Long userId);

	@Query("select t from UserPoint t where t.userId = ?1 order by t.createTime desc")
	Page<UserPoint> getPoints(Long userId, Pageable pageable);

	@Query("select count(t) > 0 from UserPoint t where t.taskItemNo = ?1 and t.userId= ?2")
	boolean exists(String taskItemNo, Long userId);
}

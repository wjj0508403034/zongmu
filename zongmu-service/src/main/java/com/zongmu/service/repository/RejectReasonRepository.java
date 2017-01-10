package com.zongmu.service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.RejectReason;

@Repository
public interface RejectReasonRepository extends CrudRepository<RejectReason, Long> {

	@Query("select count(t) > 0 from RejectReason t where t.description = ?1")
	boolean exist(String description);

	@Query("select count(t) > 0 from RejectReason t where t.isDefault = 1")
	boolean hasDefault();

	@Modifying
	@Query("update RejectReason t set t.isDefault = 0 where t.id <> ?1")
	void setOthersUnDefault(Long id);

	@Modifying
	@Query("update RejectReason t set t.isDefault = 1 where t.id = ?1")
	void setDefault(Long id);

	@Query(value = "SELECT * FROM RejectReason LIMIT 1", nativeQuery = true)
	RejectReason first();
}

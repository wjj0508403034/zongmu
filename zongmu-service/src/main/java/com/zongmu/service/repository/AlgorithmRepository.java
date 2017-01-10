package com.zongmu.service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.Algorithm;

@Repository
public interface AlgorithmRepository extends CrudRepository<Algorithm, Long> {

	@Query("select count(t) > 0 from Algorithm t where t.name = ?1")
	boolean exists(String name);

	@Query("select count(t) > 0 from Algorithm t where t.name = ?1 and t.id <> ?2")
	boolean exists(String name, Long id);

	@Modifying
	@Query("delete from Algorithm t where t.id = ?1")
	void delete(Long id);
}

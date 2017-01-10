package com.zongmu.service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.Train;

@Repository
public interface TrainRepository extends PagingAndSortingRepository<Train, Long>{

	@Query("select count(t) > 0 from Train t where t.subject = ?1")
	boolean existsCheckBeforeCreate(String name);
	
	@Query("select count(t) > 0 from Train t where t.subject = ?1 and t.id <> ?2")
	boolean existsCheckBeforeUpdate(String name,Long trainId);
}

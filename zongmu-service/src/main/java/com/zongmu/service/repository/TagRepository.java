package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.Tag;

@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {

	@Query("select count(t) > 0 from Tag t where t.name = ?1 and t.algorithmId = ?2")
	boolean exists(String name, Long algorithmId);
	
	@Query("select count(t) > 0 from Tag t where t.name = ?1 and t.algorithmId = ?2 and t.id <> ?3")
	boolean exists(String name, Long algorithmId, Long id);

	@Query("select t from Tag t where t.algorithmId = ?1")
	List<Tag> getTagsByAlgorithm(Long algorithmId);

	@Modifying
	@Query("delete from Tag t where t.algorithmId = ?1")
	void deleteTagsByAlgorithm(Long algorithmId);
}

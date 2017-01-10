package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.ViewTag;

@Repository
public interface ViewTagRepository extends PagingAndSortingRepository<ViewTag, Long> {

	@Query("select count(t) > 0 from ViewTag t where t.algorithmId = ?1 and t.name = ?2")
	boolean exists(Long algorithmId, String name);

	@Query("select count(t) > 0 from ViewTag t where t.id <> ?1 and t.algorithmId = ?2 and t.name = ?3")
	boolean checkOnUpdate(Long id, Long algorithmId, String name);

	@Query("select t from ViewTag t where t.algorithmId = ?1")
	List<ViewTag> getViewTags(Long algorithmId);
	
	@Query("select t from ViewTag t")
	List<ViewTag> getAllViewTags();
	
	@Modifying
	@Query("delete from ViewTag t where t.algorithmId = ?1")
	void deleteViewTagByAlgorithmId(Long algorithmId);
}

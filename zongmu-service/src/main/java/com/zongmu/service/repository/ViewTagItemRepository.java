package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.ViewTagItem;

@Repository
public interface ViewTagItemRepository extends PagingAndSortingRepository<ViewTagItem, Long> {

	@Query("select t from ViewTagItem t where t.viewTagId = ?1")
	List<ViewTagItem> getItems(Long viewTagId);

	@Query("select count(t) > 0 from ViewTagItem t where t.viewTagId = ?1 and t.name = ?2")
	boolean checkExistsOnCreate(Long viewTagId, String name);

	@Query("select count(t) > 0 from ViewTagItem t where t.viewTagId = ?1 and t.name = ?3 and t.id <> ?2")
	boolean checkExistsOnUpdate(Long viewTagId, Long id, String name);
	
	@Modifying
	@Query("delete from ViewTagItem t where t.viewTagId = ?1")
	void deleteViewTagItemsByViewTagId(Long viewTagId);
}

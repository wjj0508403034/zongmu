package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.TagItem;

@Repository
public interface TagItemRepository extends PagingAndSortingRepository<TagItem, Long> {

	@Query("select t from TagItem t where t.id = ?1 and t.tagId = ?2")
	TagItem getTagItem(Long id, Long tagId);

	@Query("select t from TagItem t where t.tagId = ?1 and t.value = ?2")
	TagItem getTagItem(Long tagId, String value);

	@Query("select t from TagItem t where t.tagId = ?1")
	List<TagItem> getTagItems(Long tagId);

	@Modifying
	@Query("delete from TagItem t where t.tagId = ?1")
	void deleteTagItems(Long tagId);

	@Modifying
	@Query("update TagItem t set t.isDefault = ?2 where t.tagId = ?1")
	void resetDefaultValue(Long tagId, boolean isDefault);

	@Modifying
	@Query("update TagItem t set t.isDefault = 1 where t.tagId = ?1 and t.id = ?2")
	void setDefault(Long tagId, Long tagItemId);
	
	@Modifying
	@Query("update TagItem t set t.isDefault = 1 where t.tagId = ?1 and t.id in (?2)")
	void setMultiDefault(Long tagId, List<Long> tagItemIds);
	
	@Modifying
	@Query("update TagItem t set t.isDefault = 0 where t.tagId = ?1")
	void setAllUnDefault(Long tagId);

	@Modifying
	@Query("update TagItem t set t.isDefault = 0 where t.tagId = ?1 and t.id <> ?2")
	void setOthersUnDefault(Long tagId, Long tagItemId);

	@Query(value = "SELECT * FROM TagItem where tagId = ?1 LIMIT 1", nativeQuery = true)
	TagItem first(Long tagId);
	
	
	@Query("select t.id from TagItem t where t.tagId = ?1")
	List<Long> getTagItemIds(Long tagId);
}

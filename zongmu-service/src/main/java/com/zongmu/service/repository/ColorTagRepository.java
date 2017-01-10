package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.ColorTag;

@Repository
public interface ColorTagRepository extends PagingAndSortingRepository<ColorTag, Long> {

	@Query("select t from ColorTag t where t.name = ?1")
	ColorTag getColorTag(String name);

	@Query("select count(t) from ColorTag t where t.name = ?1")
	Long countByName(String name);

	@Query("select t from ColorTag t where t.colorGroupId = ?1")
	List<ColorTag> getTagsByGroup(Long colorGroupId);

	@Query("select count(t) > 0 from ColorTag t where t.name = ?2 and t.id <> ?3 and t.colorGroupId = ?1")
	boolean existsCheckBeforeUpdate(Long colorGroupId,String name, Long id);
	
	@Query("select count(t) > 0 from ColorTag t where t.name = ?2 and t.colorGroupId = ?1")
	boolean existsCheckBeforeCreate(Long colorGroupId,String name);

	@Modifying
	@Query("delete from ColorTag t where t.colorGroupId = ?1")
	void deleteByGroupId(Long groupId);
	
}

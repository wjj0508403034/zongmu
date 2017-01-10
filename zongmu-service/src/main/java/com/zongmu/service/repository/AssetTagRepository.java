package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.dto.tag.TagCategory;
import com.zongmu.service.entity.AssetTag;

@Repository
public interface AssetTagRepository extends PagingAndSortingRepository<AssetTag, Long> {
    
    @Query("select count(t) > 0 from AssetTag t where t.name = ?1")
    boolean exists(String name);
    
    @Query("select t from AssetTag t where t.name = ?1")
    AssetTag getAssetTag(String name);

    @Query("select t from AssetTag t")
    List<AssetTag> getAssetTags();

    @Modifying
	@Query("update AssetTag t set t.isDefault = 1 where t.id = ?1 and t.category = ?2")
	void setDefault(Long id, TagCategory category);

    @Modifying
   	@Query("update AssetTag t set t.isDefault = 0 where t.id <> ?1 and t.category = ?2")
	void setOthersUnDefault(Long id, TagCategory category);
    
    @Query("select count(t) > 0 from AssetTag t where t.category = ?1")
    boolean exists(TagCategory category);
}

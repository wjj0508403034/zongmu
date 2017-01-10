package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.AssetRefAssetTag;

@Repository
public interface AssetRefAssetTagRepository extends PagingAndSortingRepository<AssetRefAssetTag, Long>{

    @Query("select t from AssetRefAssetTag t where t.assetId = ?1")
    List<AssetRefAssetTag> getAssetRefAssetTags(Long assetId);
    
    @Modifying
    @Query("delete from AssetRefAssetTag t where t.assetId = ?1")
    void deleteTags(Long assetId);
    
	
	@Query("select count(t) from AssetRefAssetTag t where t.assetTagId = ?1")
	Long countByAssetTag(Long assetTagId);
}

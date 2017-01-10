package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.Asset2AssetViewTag;

@Repository
public interface Asset2AssetViewTagRepository extends PagingAndSortingRepository<Asset2AssetViewTag, Long> {

	@Query("select t from Asset2AssetViewTag t where t.assetId = ?1")
	List<Asset2AssetViewTag> getListByAssetId(Long assetId);

	@Modifying
	@Query("delete from Asset2AssetViewTag t where t.assetId = ?1")
	void deleteAssetViewTag(Long assetId);

	@Query("select count(t) > 0 from Asset2AssetViewTag t where t.assetViewTagId = ?1")
	boolean isAssetViewTagUsed(Long assetViewTagId);

	@Query("select count(t) > 0 from Asset2AssetViewTag t where t.assetViewTagItemId = ?1")
	boolean isAssetViewTagItemUsed(Long assetViewTagItemId);
}

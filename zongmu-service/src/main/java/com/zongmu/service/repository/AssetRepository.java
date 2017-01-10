package com.zongmu.service.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.Asset;

@Repository
public interface AssetRepository extends PagingAndSortingRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {
	
	@Query("select t from Asset t where t.assetNo = ?1")
	Asset getAsset(String assetNo);

	@Modifying
	@Query("delete from Asset t where t.assetNo = ?1")
	void deleteAsset(String assetNo);
	
	@Query("select count(t) > 0 from Asset t where t.name = ?1")
	boolean checkNameDupBeforeCreate(String assetName);

}

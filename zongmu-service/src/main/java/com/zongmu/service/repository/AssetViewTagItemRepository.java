package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.AssetViewTagItem;

@Repository
public interface AssetViewTagItemRepository extends CrudRepository<AssetViewTagItem, Long> {

	@Query("select count(t) > 0 from AssetViewTagItem t where t.assetViewTagId = ?1 and t.name = ?2")
	boolean exists(Long assetViewTagId, String name);

	@Query("select count(t) from AssetViewTagItem t where t.assetViewTagId = ?1")
	Long count(Long assetViewTagId);

	@Modifying
	@Query("update AssetViewTagItem t set t.isDefault = 0 where t.assetViewTagId = ?1 and t.id <> ?2")
	void setUnDefault(Long assetViewTagId, Long id);
	
	@Query("select t from AssetViewTagItem t where t.assetViewTagId = ?1")
	List<AssetViewTagItem> getItems(Long assetViewTagId);
}

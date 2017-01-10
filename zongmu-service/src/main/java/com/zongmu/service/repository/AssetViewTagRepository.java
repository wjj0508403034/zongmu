package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.AssetViewTag;

@Repository
public interface AssetViewTagRepository extends CrudRepository<AssetViewTag, Long> {

	@Query("select count(t) > 0 from AssetViewTag t where t.name = ?1")
	boolean exists(String name);

	@Query("select count(t) > 0 from AssetViewTag t where t.name = ?2 and t.id <> ?1")
	boolean existsOnUpdate(Long id, String name);

	@Query("select t from AssetViewTag t")
	List<AssetViewTag> getAll();
}

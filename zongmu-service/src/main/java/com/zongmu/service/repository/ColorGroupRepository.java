package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.ColorGroup;

@Repository
public interface ColorGroupRepository extends PagingAndSortingRepository<ColorGroup, Long> {

	@Query("select count(t) > 0 from ColorGroup t where t.algorithmId = ?1")
	boolean exists(Long algorithmId);
	
	@Query("select t from ColorGroup t where t.algorithmId = ?1")
	ColorGroup getGroupByAlgorithm(Long algorithmId);
	
	@Query("select t from ColorGroup t where t.algorithmId = ?1")
	List<ColorGroup> getGroups(Long algorithmId);

	@Modifying
	@Query("delete from ColorGroup t where t.algorithmId = ?1")
	void deleteGroupByAlgorithm(Long algorithmId);
}

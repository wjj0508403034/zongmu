package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.Timeline;

@Repository
public interface TimelineRepository extends PagingAndSortingRepository<Timeline, Long> {

    @Query("select t from Timeline t where t.markShapeId = ?1")
    public List<Timeline> getTimelines(Long markShapeId);
    
    @Modifying
    @Query("delete from Timeline t where t.markShapeId = ?1")
    void deleteTimelines(Long markShapeId);
}

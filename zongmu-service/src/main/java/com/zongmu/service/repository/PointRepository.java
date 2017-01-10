package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.zongmu.service.entity.Point;

@Repository
public interface PointRepository extends PagingAndSortingRepository<Point, Long> {

    @Query("select p from Point p where p.shapeId = ?1")
    List<Point> findPoints(Long shapeId);
    
    @Query("select p from Point p where p.timelineId = ?1")
    List<Point> getPoints(Long timelineId);

    @Modifying
    @Query("delete from Point p where p.timelineId = ?1")
    void deletePoints(Long timelineId);
}

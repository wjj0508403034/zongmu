package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.MarkShape;

@Repository
public interface MarkShapeRepository extends PagingAndSortingRepository<MarkShape, Long> {

    @Query("select t from MarkShape t where t.taskItemFileMarkId = ?1")
    List<MarkShape> getMarkShapes(Long taskItemFileMarkId);

    @Modifying
    @Query("delete from MarkShape t where t.taskItemFileMarkId = ?1")
    void deleteMarkShapes(Long taskItemFileMarkId);
}

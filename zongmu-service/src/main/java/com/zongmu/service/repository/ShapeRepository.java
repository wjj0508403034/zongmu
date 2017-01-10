package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.Shape;

@Repository
public interface ShapeRepository extends PagingAndSortingRepository<Shape, Long> {

    @Query("select t from Shape t where t.markId = ?1")
    List<Shape> findShapes(Long markId);

    @Modifying
    @Transactional
    @Query("delete from Shape t where t.markId = ?1")
    void deleteShapes(Long markId);
}

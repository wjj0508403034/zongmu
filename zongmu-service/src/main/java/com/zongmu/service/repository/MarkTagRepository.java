package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.MarkTag;

@Repository
public interface MarkTagRepository extends PagingAndSortingRepository<MarkTag, Long> {

    @Query("select t from MarkTag t where t.markShapeId = ?1")
    List<MarkTag> getTags(Long markShapeId);

    @Modifying
    @Transactional
    @Query("delete from MarkTag t where t.markShapeId = ?1")
    void deleteTags(Long markShapeId);
}

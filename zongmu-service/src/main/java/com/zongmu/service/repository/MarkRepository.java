package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.Mark;

@Repository
public interface MarkRepository extends PagingAndSortingRepository<Mark, Long> {

    List<Mark> findByTaskRecordNo(String taskRecordNo);

    @Query("select t from Mark t where t.taskRecordNo = ?1 and t.frameIndex = ?2")
    List<Mark> findMarks(String taskRecordNo, float frameIndex);
}

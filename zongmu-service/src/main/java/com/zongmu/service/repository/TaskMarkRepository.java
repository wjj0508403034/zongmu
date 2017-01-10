package com.zongmu.service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.zongmu.service.entity.TaskMark;

@Repository
public interface TaskMarkRepository extends PagingAndSortingRepository<TaskMark, Long> {

    @Query("select t from TaskMark t where t.taskRecordId = ?1")
    TaskMark getTaskMark(Long taskRecordId);

    @Modifying
    @Query("delete from TaskMark t where t.taskRecordId = ?1")
    void deleteTaskMarks(Long taskRecordId);
}

package com.zongmu.ffmpeg.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.ffmpeg.entities.TaskItem;

@Repository
public interface TaskItemRepo extends PagingAndSortingRepository<TaskItem, Long> {

    @Query(value = "select * from TaskItem where status = 0 LIMIT ?", nativeQuery = true)
    List<TaskItem> getPendingTaskItems(int count);
    
    @Query(value = "select * from TaskItem where status = 4 LIMIT ?", nativeQuery = true)
    List<TaskItem> getFailedTaskItems(int count);
   
}

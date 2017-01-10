package com.zongmu.ffmpeg.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.ffmpeg.entities.Task;

@Repository
public interface TaskRepo extends PagingAndSortingRepository<Task, Long> {

    @Query("select t from Task t where t.taskNo = ?1")
    Task getTask(String taskNo);

    @Query("select t from Task t where t.id = ?1")
    Task getTask(Long taskId);
    
    @Query("select task from Task task where task.id in (select taskItem.taskId from TaskItem taskItem where taskItem.taskItemNo = ?1 )")
    List<Task> getTaskByTaskItemNo(String taskItemNo);
}

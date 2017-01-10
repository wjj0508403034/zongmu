package com.zongmu.ffmpeg.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.ffmpeg.entities.TaskItemFile;

@Repository
public interface TaskItemFileRepo extends PagingAndSortingRepository<TaskItemFile, Long> {

    @Query("select count(t) from TaskItemFile t where t.status = 0 and t.taskItemNo = ?1")
    Long getTaskItemFileSuccessCount(String taskItemNo);
    
    @Query(value = "select * from TaskItemFile where uploadStatus = 0 LIMIT ?", nativeQuery = true)
    List<TaskItemFile> getPendingUploadFiles(int count);
}

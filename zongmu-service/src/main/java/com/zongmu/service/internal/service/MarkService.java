package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.entity.Mark;
import com.zongmu.service.exception.BusinessException;

public interface MarkService {

    List<Mark> getMarks(String taskRecordNo);
    
    Mark getMarkByTaskItemNo(String taskItemNo);

    void saveMark(Mark mark) throws BusinessException;

    void saveMarks(List<Mark> marks);

    void updateMark(Mark mark);
}

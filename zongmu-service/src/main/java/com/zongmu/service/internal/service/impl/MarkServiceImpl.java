package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.Mark;
import com.zongmu.service.entity.Shape;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.MarkService;
import com.zongmu.service.internal.service.ShapeService;
import com.zongmu.service.repository.MarkRepository;

@Transactional
@Service
public class MarkServiceImpl implements MarkService {

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private ShapeService shapeService;

    @Override
    public List<Mark> getMarks(String taskRecordNo) {
        List<Mark> marks = this.markRepository.findByTaskRecordNo(taskRecordNo);
        for (Mark mark : marks) {
            mark.setShapes(this.shapeService.getShapes(mark.getId()));
        }
        return marks;
    }

    @Override
    public void saveMark(Mark mark) throws BusinessException {
        List<Mark> marks = this.markRepository.findMarks(mark.getTaskRecordNo(), mark.getFrameIndex());
        for (Mark markItem : marks) {
            this.deleteMark(markItem);
        }

        Mark newMark = this.markRepository.save(mark);
        for (Shape shape : mark.getShapes()) {
            shape.setMarkId(newMark.getId());
            this.shapeService.createShape(shape);
        }
    }

    private void deleteMark(Mark mark) {
        this.shapeService.deleteShapes(mark.getId());
        this.markRepository.delete(mark);
    }

    @Override
    public void saveMarks(List<Mark> marks) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateMark(Mark mark) {
        // TODO Auto-generated method stub

    }

    @Override
    public Mark getMarkByTaskItemNo(String taskItemNo) {
        List<Mark> marks = this.markRepository.findByTaskRecordNo(taskItemNo);
        if (marks.size() > 0) {
            Mark mark = marks.get(0);
            mark.setShapes(this.shapeService.getShapes(mark.getId()));
            return mark;
        }

        return null;
    }

}

package com.zongmu.service.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.MarkTag;
import com.zongmu.service.entity.Point;
import com.zongmu.service.entity.Shape;
import com.zongmu.service.internal.service.ShapeService;
import com.zongmu.service.repository.MarkTagRepository;
import com.zongmu.service.repository.PointRepository;
import com.zongmu.service.repository.ShapeRepository;

@Transactional
@Service
public class ShapeServiceImpl implements ShapeService {

    @Autowired
    private ShapeRepository shapeRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private MarkTagRepository markTagRepository;

    @Override
    public List<Shape> getShapes(Long markId) {
        List<Shape> shapes = this.shapeRepository.findShapes(markId);
        for (Shape shape : shapes) {
            shape.setPoints(this.pointRepository.findPoints(shape.getId()));
            //shape.setTags(this.markTagRepository.getTags(shape.getId()));
        }
        return shapes;
    }

    @Override
    public void createShape(Shape shape) {
        Shape newShape = this.shapeRepository.save(shape);
        for (Point point : shape.getPoints()) {
            point.setShapeId(newShape.getId());
            this.pointRepository.save(point);
        }

        for (MarkTag tag : shape.getTags()) {
            //tag.setShapeId(newShape.getId());
            this.markTagRepository.save(tag);
        }
    }

    @Override
    public void deleteShapes(Long markId) {
        List<Shape> shapes = this.shapeRepository.findShapes(markId);
        for (Shape shape : shapes) {
            this.pointRepository.deletePoints(shape.getId());
            //this.markTagRepository.deleteTags(shape.getId());
        }
        this.shapeRepository.deleteShapes(markId);
    }
}

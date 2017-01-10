package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.entity.Shape;

public interface ShapeService {

    List<Shape> getShapes(Long markId);

    void createShape(Shape shape);

    void deleteShapes(Long markId);
}

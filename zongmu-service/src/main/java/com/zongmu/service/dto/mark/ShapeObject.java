package com.zongmu.service.dto.mark;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.service.dto.DTOObject;

public class ShapeObject extends DTOObject {

    private static final long serialVersionUID = 6261383415848621714L;

    private Long id;
    private String shapeId;
    private String shapeName;
    private int sideCount;
    private ShapeType type;
    private String color;

    private List<PointObject> points = new ArrayList<>();

    private List<MarkTagObject> tags = new ArrayList<>();

    public ShapeType getType() {
        return type;
    }

    public void setType(ShapeType type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<PointObject> getPoints() {
        return points;
    }

    public void setPoints(List<PointObject> points) {
        this.points = points;
    }

    public List<MarkTagObject> getTags() {
        return tags;
    }

    public void setTags(List<MarkTagObject> tags) {
        this.tags = tags;
    }

    public int getSideCount() {
        return sideCount;
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }

    public String getShapeName() {
        return shapeName;
    }

    public void setShapeName(String shapeName) {
        this.shapeName = shapeName;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

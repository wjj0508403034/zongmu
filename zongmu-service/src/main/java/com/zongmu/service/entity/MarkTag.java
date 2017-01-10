package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class MarkTag implements Serializable {

    private static final long serialVersionUID = 7657158543739862301L;

    @Id
    @SequenceGenerator(name = "MARK_TAG_SEQUENCE", sequenceName = "MARK_TAG_SEQUENCE")
    @GeneratedValue(generator = "MARK_TAG_SEQUENCE",strategy = GenerationType.AUTO)
    private long id;
    private Long tagId;
    private String tagName;
    private Long tagItemId;
    private String tagItemValue;
    private Long markShapeId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Long getTagItemId() {
        return tagItemId;
    }

    public void setTagItemId(Long tagItemId) {
        this.tagItemId = tagItemId;
    }

    public String getTagItemValue() {
        return tagItemValue;
    }

    public void setTagItemValue(String tagItemValue) {
        this.tagItemValue = tagItemValue;
    }

    public Long getMarkShapeId() {
        return markShapeId;
    }

    public void setMarkShapeId(Long markShapeId) {
        this.markShapeId = markShapeId;
    }
}

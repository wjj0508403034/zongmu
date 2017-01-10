package com.zongmu.service.dto.mark;

import com.zongmu.service.dto.DTOObject;

public class MarkTagObject extends DTOObject {

    private static final long serialVersionUID = -844069921286371144L;

    private Long tagId;

    private String tagName;

    private Long tagItemId;

    private String tagItemValue;

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

}

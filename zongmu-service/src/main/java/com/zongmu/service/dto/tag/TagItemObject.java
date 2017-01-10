package com.zongmu.service.dto.tag;

import com.zongmu.service.dto.DTOObject;

public class TagItemObject extends DTOObject {
    private static final long serialVersionUID = -4083912544381075676L;

    private Long id;
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

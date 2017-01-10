package com.zongmu.service.dto.tag;

import java.util.ArrayList;
import java.util.List;

import com.zongmu.service.dto.DTOObject;

public class TagObject extends DTOObject {

    private static final long serialVersionUID = 5655762606633936709L;
    private Long id;
    private String name;
    private List<TagItemObject> items = new ArrayList<TagItemObject>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TagItemObject> getItems() {
        return items;
    }

    public void setItems(List<TagItemObject> items) {
        this.items = items;
    }
}

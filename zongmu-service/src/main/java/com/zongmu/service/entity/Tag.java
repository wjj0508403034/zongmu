package com.zongmu.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.zongmu.service.dto.tag.TagControlType;

@Entity
@Table
public class Tag implements Serializable {

    private static final long serialVersionUID = 5377766022659049221L;

    @Id
    @SequenceGenerator(name = "TAG_SEQUENCE", sequenceName = "TAG_SEQUENCE")
    @GeneratedValue(generator = "TAG_SEQUENCE",strategy = GenerationType.AUTO)
    private Long id;
    
    private TagControlType type;

    private String name;
    
    private Long algorithmId;

    @Transient
    private List<TagItem> items = new ArrayList<>();

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

    public List<TagItem> getItems() {
        return items;
    }

    public void setItems(List<TagItem> items) {
        this.items = items;
    }

	public TagControlType getType() {
		return type;
	}

	public void setType(TagControlType type) {
		this.type = type;
	}

	public Long getAlgorithmId() {
		return algorithmId;
	}

	public void setAlgorithmId(Long algorithmId) {
		this.algorithmId = algorithmId;
	}

}

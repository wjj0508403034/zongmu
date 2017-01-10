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
public class AssetRefAssetTag implements Serializable {

    private static final long serialVersionUID = 173375520815462576L;

    @Id
    @SequenceGenerator(name = "ASSETREFASSETTAG_SEQUENCE", sequenceName = "ASSETREFASSETTAG_SEQUENCE")
    @GeneratedValue(generator = "ASSETREFASSETTAG_SEQUENCE",strategy = GenerationType.AUTO)
    private Long id;

    private Long assetId;
    private Long assetTagId;
    private String assetTagName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public Long getAssetTagId() {
        return assetTagId;
    }

    public void setAssetTagId(Long assetTagId) {
        this.assetTagId = assetTagId;
    }

    public String getAssetTagName() {
        return assetTagName;
    }

    public void setAssetTagName(String assetTagName) {
        this.assetTagName = assetTagName;
    }

}

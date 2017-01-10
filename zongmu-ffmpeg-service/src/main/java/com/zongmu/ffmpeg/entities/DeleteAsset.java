package com.zongmu.ffmpeg.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class DeleteAsset {

    @Id
    @SequenceGenerator(name = "DeleteAsset_SEQUENCE", sequenceName = "DeleteAsset_SEQUENCE")
    @GeneratedValue(generator = "DeleteAsset_SEQUENCE", strategy = GenerationType.AUTO)
    private Long id;

    private String assetNo;

    private DeleteAssetStatus status;

    public Long getId() {
        return id;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public DeleteAssetStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public void setStatus(DeleteAssetStatus status) {
        this.status = status;
    }
}

package com.zongmu.ffmpeg.compress.dto;

import java.util.ArrayList;
import java.util.List;

public class AssetInfo {
    private String assetNo;
    private List<AssetFileInfo> assetFileInfos = new ArrayList<>();

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public List<AssetFileInfo> getAssetFileInfos() {
        return assetFileInfos;
    }

    public void setAssetFileInfos(List<AssetFileInfo> assetFileInfos) {
        this.assetFileInfos = assetFileInfos;
    }
}

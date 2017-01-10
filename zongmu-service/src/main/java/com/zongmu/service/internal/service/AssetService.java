package com.zongmu.service.internal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.zongmu.service.criteria.QueryParams;
import com.zongmu.service.dto.asset.AssetFileStatus;
import com.zongmu.service.dto.asset.AssetTagParam;
import com.zongmu.service.dto.asset.AssetViewTagParam;
import com.zongmu.service.dto.asset.FileData;
import com.zongmu.service.dto.asset.PicData;
import com.zongmu.service.dto.asset.Status;
import com.zongmu.service.dto.search.AssetSearchParam;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.Asset2AssetViewTag;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.video.VideoInfo;

public interface AssetService {

	Page<Asset> getAssets(QueryParams queryParams, Pageable pageable);

	Asset getSimpleAsset(String assetNo) throws BusinessException;

	void update(Asset asset);

	void setStatus(String assetNo, Status status) throws BusinessException;

	Asset createAsset(Asset asset) throws BusinessException;

	void attachFileToAsset(String assetNo, MultipartFile file) throws BusinessException;

	void saveAssetFile(AssetFile assetFile);

	void compress(String assetNo) throws BusinessException;

	void updateStatus(Asset asset, AssetFileStatus status);

	void updateStatus(String assetFileNo, AssetFileStatus status) throws BusinessException;

	void setAssetTags(String assetNo, AssetTagParam tagParam) throws BusinessException;

	void afterUploadFTP(AssetFile assetFile, boolean success);
	
	void setAssetFileStatus(AssetFile assetFile,AssetFileStatus assetFileStatus);

	void setVideoInfo(AssetFile assetFile, VideoInfo videoInfo);

	void retryFtpUpload();

	void deleteAsset(String assetNo) throws BusinessException;

	AssetFile getAssetFileByNo(String assetFileNo);

	Long countByAssetTag(Long assetTagId);

	Asset getAssetDetail(String assetNo) throws BusinessException;

	Asset getAssetWithFiles(String assetNo) throws BusinessException;

	void updateAssetViewTags(String assetNo, AssetViewTagParam tagParam) throws BusinessException;

	void setUploadFileData(String assetNo, FileData fileData) throws BusinessException;

	void setFourUploadFileData(String assetNo, FileData[] fileDatas) throws BusinessException;

	List<Asset2AssetViewTag> getAssetViewTags(Long assetId);

	void setUploadPicData(String assetNo, PicData[] picDatas) throws BusinessException;

	Page<Asset> queryAssets(AssetSearchParam assetSearchParam, Pageable pageable);
	
	List<AssetFile> getPendingUploadVideoFiles(int count);
}

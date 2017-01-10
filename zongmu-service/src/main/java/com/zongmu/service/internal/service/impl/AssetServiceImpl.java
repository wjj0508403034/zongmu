package com.zongmu.service.internal.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zongmu.service.criteria.Filter;
import com.zongmu.service.criteria.OrderBy;
import com.zongmu.service.criteria.QueryParams;
import com.zongmu.service.dto.asset.AssetFileStatus;
import com.zongmu.service.dto.asset.AssetTagParam;
import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.asset.AssetViewTagParam;
import com.zongmu.service.dto.asset.DeleteAssetStatus;
import com.zongmu.service.dto.asset.FileData;
import com.zongmu.service.dto.asset.PicData;
import com.zongmu.service.dto.asset.Status;
import com.zongmu.service.dto.search.AssetSearchParam;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.entity.Asset2AssetViewTag;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.AssetTag;
import com.zongmu.service.entity.DeleteAsset;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.AssetService;
import com.zongmu.service.internal.service.AssetTagService;
import com.zongmu.service.internal.service.AssetViewTagService;
import com.zongmu.service.internal.service.FtpService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.properties.FtpProperties;
import com.zongmu.service.repository.Asset2AssetViewTagRepository;
import com.zongmu.service.repository.AssetFileRepository;
import com.zongmu.service.repository.AssetRefAssetTagRepository;
import com.zongmu.service.repository.AssetRepository;
import com.zongmu.service.repository.DeleteAssetRepository;
import com.zongmu.service.runnable.UploadFileToFtp;
import com.zongmu.service.specification.AssetSpecification;
import com.zongmu.service.util.CommonService;
import com.zongmu.service.util.FileService;
import com.zongmu.service.util.ThreadPoolService;
import com.zongmu.service.video.VideoInfo;
import com.zongmu.service.video.VideoService;

@Service
public class AssetServiceImpl implements AssetService {
	private static Logger logger = Logger.getLogger(AssetServiceImpl.class);

	@Autowired
	private AssetRepository assetRepo;

	@Autowired
	private AssetFileRepository assetFileRepo;

	@Autowired
	private AssetRefAssetTagRepository assetRefAssetTagRepo;

	@Autowired
	private Asset2AssetViewTagRepository asset2AssetViewTagRepo;

	@Autowired
	private CommonService commonService;

	@Autowired
	private AssetTagService assetTagService;

	@Autowired
	private ThreadPoolService threadPoolService;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private FileService fileService;

	@Autowired
	private VideoService videoService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private AssetViewTagService assetViewTagService;

	@Autowired
	private DeleteAssetRepository deleteAssetRepo;

	@Override
	public Long countByAssetTag(Long assetTagId) {
		return this.assetRefAssetTagRepo.countByAssetTag(assetTagId);
	}

	@Autowired
	private FtpProperties ftpProperties;

	private String getAssetFolder(String assetNo) {
		return "/upload/" + assetNo + "/";
	}

	@Override
	public Asset createAsset(Asset asset) throws BusinessException {
		if (this.assetRepo.checkNameDupBeforeCreate(asset.getName())) {
			throw new BusinessException(ErrorCode.ASSET_NAME_DUP);
		}
		logger.info("Create asset ...");
		asset.setStatus(Status.UPLOADING);
		asset.setAssetNo(this.commonService.generateNo());
		asset.setAssetType(asset.getAssetType());
		asset.setMemo(asset.getMemo());
		asset.setCreateTime(DateTime.now());
		asset.setTimeOfDay(this.getLocalTime(asset.getRecordTime()));
		logger.info("Create asset successfully.");
		this.assetRepo.save(asset);
		for (Asset2AssetViewTag viewTag : asset.getViewTags()) {
			viewTag.setAssetId(asset.getId());
			this.asset2AssetViewTagRepo.save(viewTag);
		}

		asset.setFtpFolder(this.getAssetFolder(asset.getAssetNo()));
		asset.setFullFtpFolder("ftp://" + this.ftpProperties.getFtpServiceUrl() + asset.getFtpFolder());
		return asset;
	}

	@Transactional
	@Override
	public void deleteAsset(String assetNo) throws BusinessException {
		Asset asset = this.assetRepo.getAsset(assetNo);
		if (asset == null) {
			throw new BusinessException(ErrorCode.ASSET_NOT_FOUND);
		}
		this.assetRepo.deleteAsset(assetNo);
		this.assetFileRepo.deleteAssetFiles(assetNo);
		this.asset2AssetViewTagRepo.deleteAssetViewTag(asset.getId());
		this.taskService.deleteTaskByAsset(asset);
		DeleteAsset deleteAsset = new DeleteAsset();
		deleteAsset.setAssetNo(assetNo);
		deleteAsset.setStatus(DeleteAssetStatus.NEW);
		this.deleteAssetRepo.save(deleteAsset);
	}

	@Transactional
	@Override
	public void updateAssetViewTags(String assetNo, AssetViewTagParam tagParam) throws BusinessException {
		Asset asset = this.assetRepo.getAsset(assetNo);
		if (asset == null) {
			throw new BusinessException(ErrorCode.ASSET_NOT_FOUND);
		}

		this.asset2AssetViewTagRepo.deleteAssetViewTag(asset.getId());
		for (Asset2AssetViewTag viewTag : tagParam.getItems()) {
			viewTag.setAssetId(asset.getId());
			this.asset2AssetViewTagRepo.save(viewTag);
		}

		this.taskService.updateAssetViewTagForAllTasks(asset, tagParam.getItems());
	}

	private int getLocalTime(DateTime dateTime) {
		int time = dateTime.getHourOfDay() + 8;
		if (time > 24) {
			time -= 24;
		}

		return time;
	}

	@Override
	public void attachFileToAsset(String assetNo, MultipartFile file) throws BusinessException {
		Asset asset = this.getSimpleAsset(assetNo);
		AssetFile assetFile = new AssetFile();
		assetFile.setAssetNo(asset.getAssetNo());
		assetFile.setAssetFileNo(this.commonService.generateNo());
		assetFile.setFileName(file.getOriginalFilename());
		assetFile.setFileSize(file.getSize());
		assetFile.setFileType(file.getContentType());
		assetFile.setAssetFileStatus(AssetFileStatus.UPLOADING);
		assetFile.setCreateTime(DateTime.now());
		assetFile.setUpdateTime(DateTime.now());
		assetFile.setAssetType(asset.getAssetType());
		assetFile.setAsset(asset);
		assetFile = this.assetFileRepo.save(assetFile);

		this.saveAndUpload(asset, assetFile, file);
	}

	private void saveAndUpload(Asset asset, AssetFile assetFile, MultipartFile file) {
		boolean result = this.fileService.saveFile(assetFile, file);
		if (result) {
			assetFile.setAssetFileStatus(AssetFileStatus.UPLOADSUCCESS);
			assetFile = this.assetFileRepo.save(assetFile);

			if (asset.getAssetType() != AssetType.PICTURE) {
				VideoInfo videoInfo = this.videoService.getVideoInfo(assetFile);
				if (videoInfo != null) {
					assetFile.setFps(videoInfo.getFps());
					assetFile.setWidth(videoInfo.getWidth());
					assetFile.setHeight(videoInfo.getHeight());
					assetFile.setDuration(videoInfo.getDuration());
					assetFile.setUpdateTime(DateTime.now());
					assetFile.setFrames(videoInfo.getFramesCount());
					this.assetFileRepo.save(assetFile);

					if (asset.getRecordLength() == null) {
						// asset.setRecordLength(Math.round(videoInfo.getDuration()
						// / 60));
						asset.setRecordLength(videoInfo.getDuration());
						this.assetRepo.save(asset);
					}
					// this.threadPoolService.run(new
					// UploadFileToFtp(applicationContext, assetFile));
				} else {
					assetFile.setAssetFileStatus(AssetFileStatus.UPLOADFAILED);
					assetFile.setUpdateTime(DateTime.now());
					this.assetFileRepo.save(assetFile);
				}

			} else {
				this.threadPoolService.run(new UploadFileToFtp(applicationContext, assetFile));
			}

		} else {
			assetFile.setAssetFileStatus(AssetFileStatus.UPLOADFAILED);
			this.assetFileRepo.save(assetFile);
		}
	}

	@Override
	public void setUploadPicData(String assetNo, PicData[] picDatas) throws BusinessException {
		Asset asset = this.getSimpleAsset(assetNo);

		for (PicData picData : picDatas) {
			this.createAssetFile(asset, picData);
		}
	}

	@Override
	public void setUploadFileData(String assetNo, FileData fileData) throws BusinessException {
		Asset asset = this.getSimpleAsset(assetNo);

		asset.setRecordLength(
				this.calcDuration(fileData.getVideoInfo().getFps(), fileData.getVideoInfo().getFramesCount()));
		this.assetRepo.save(asset);
		this.createAssetFile(asset, fileData);
	}

	@Override
	public void setFourUploadFileData(String assetNo, FileData[] fileDatas) throws BusinessException {
		Asset asset = this.getSimpleAsset(assetNo);
		if (fileDatas.length != 4) {
			throw new BusinessException(ErrorCode.File_Must_be_Four);
		}
		asset.setRecordLength(
				this.calcDuration(fileDatas[0].getVideoInfo().getFps(), fileDatas[0].getVideoInfo().getFramesCount()));
		this.assetRepo.save(asset);
		for (FileData fileData : fileDatas) {
			fileData.getVideoInfo().setDuration(
					this.calcDuration(fileData.getVideoInfo().getFps(), fileData.getVideoInfo().getFramesCount()));
			this.createAssetFile(asset, fileData);
		}
	}

	private float calcDuration(int fps, long framesCount) {
		return (framesCount * 1.0f) / fps;
	}

	private void createAssetFile(Asset asset, PicData picData) {
		AssetFile assetFile = new AssetFile();
		assetFile.setAssetNo(asset.getAssetNo());
		assetFile.setAssetFileNo(this.commonService.generateNo());
		assetFile.setFileName(picData.getFileName());
		assetFile.setFileSize(picData.getFileSize());
		assetFile.setFileType(picData.getFileType());
		assetFile.setAssetFileStatus(AssetFileStatus.FTPUPLOADSUCCESS);
		assetFile.setCreateTime(DateTime.now());
		assetFile.setUpdateTime(DateTime.now());
		assetFile.setAssetType(asset.getAssetType());
		assetFile.setAsset(asset);
		assetFile.setUpdateTime(DateTime.now());
		assetFile = this.assetFileRepo.save(assetFile);
	}

	private void createAssetFile(Asset asset, FileData fileData) {
		AssetFile assetFile = new AssetFile();
		assetFile.setAssetNo(asset.getAssetNo());
		assetFile.setAssetFileNo(this.commonService.generateNo());
		assetFile.setFileName(fileData.getFileName());
		assetFile.setFileSize(fileData.getFileSize());
		assetFile.setFileType("video/avi");
		assetFile.setAssetFileStatus(AssetFileStatus.FTPUPLOADSUCCESS);
		assetFile.setCreateTime(DateTime.now());
		assetFile.setUpdateTime(DateTime.now());
		assetFile.setAssetType(asset.getAssetType());
		assetFile.setAsset(asset);
		assetFile.setFps(fileData.getVideoInfo().getFps());
		assetFile.setWidth(fileData.getVideoInfo().getWidth());
		assetFile.setHeight(fileData.getVideoInfo().getHeight());
		assetFile.setDuration(fileData.getVideoInfo().getDuration());
		assetFile.setUpdateTime(DateTime.now());
		assetFile = this.assetFileRepo.save(assetFile);
	}

	@Autowired
	private FtpService ftpService;

	@Override
	public void retryFtpUpload() {
		List<AssetFile> assetFiles = this.assetFileRepo.getFtpUploadFailedFiles(AssetFileStatus.FTPUPLOADFAILED);

		for (AssetFile assetFile : assetFiles) {
			logger.info("Start retry Ftp Upload ...");
			if (this.fileService.assetFileExists(assetFile)) {
				try {
					this.ftpService.upload(assetFile);
				} catch (BusinessException e) {
					logger.error("Upload failed");
					logger.error(ExceptionUtils.getStackTrace(e));
				}
				// assetFile.setAssetFileStatus(AssetFileStatus.FTPUPLOADING);
				// assetFile = this.assetFileRepo.save(assetFile);
				// this.threadPoolService.run(new
				// UploadFileToFtp(applicationContext, assetFile));
			} else {
				logger.warn("Asset file not in this machine. " + assetFile.getAssetFileNo());
			}
		}
	}

	@Override
	public void afterUploadFTP(AssetFile assetFile, boolean success) {
		assetFile.setAssetFileStatus(success ? AssetFileStatus.FTPUPLOADSUCCESS : AssetFileStatus.FTPUPLOADFAILED);
		this.assetFileRepo.save(assetFile);
		if (success) {
			this.fileService.deleteFile(assetFile);
		}
	}

	@Override
	public void setVideoInfo(AssetFile assetFile, VideoInfo videoInfo) {
		if (videoInfo != null) {
			assetFile.setFps(videoInfo.getFps());
			assetFile.setWidth(videoInfo.getWidth());
			assetFile.setHeight(videoInfo.getHeight());
			assetFile.setDuration(videoInfo.getDuration());
		} else {
			assetFile.setAssetFileStatus(AssetFileStatus.UPLOADFAILED);
		}

		assetFile.setUpdateTime(DateTime.now());
		this.assetFileRepo.save(assetFile);
	}

	@Override
	public void compress(String assetNo) throws BusinessException {
		// Asset asset = this.getAssetWithFiles(assetNo);
		// this.threadPoolService.run(new CompressVideoTask(applicationContext,
		// asset));
	}

	@Override
	public void updateStatus(Asset asset, AssetFileStatus status) {
		for (AssetFile assetFile : asset.getAssetFiles()) {
			assetFile.setAssetFileStatus(status);
			assetFile.setUpdateTime(DateTime.now());
			this.assetFileRepo.save(assetFile);
		}
	}

	@Override
	public void updateStatus(String assetFileNo, AssetFileStatus status) throws BusinessException {
		AssetFile assetFile = this.getAssetFile(assetFileNo);
		assetFile.setAssetFileStatus(status);
		assetFile.setUpdateTime(DateTime.now());
		this.assetFileRepo.save(assetFile);
	}

	private AssetFile getAssetFile(String assetFileNo) throws BusinessException {
		AssetFile assetFile = this.assetFileRepo.getAssetFileByAssetFileNo(assetFileNo);
		if (assetFile == null) {
			throw new BusinessException(ErrorCode.ASSET_FILE_NOT_FOUND);
		}

		return assetFile;
	}

	@Override
	public AssetFile getAssetFileByNo(String assetFileNo) {
		return this.assetFileRepo.getAssetFileByAssetFileNo(assetFileNo);
	}

	@Override
	public void saveAssetFile(AssetFile assetFile) {
		this.assetFileRepo.save(assetFile);
	}

	@Override
	public Page<Asset> getAssets(QueryParams queryParams, Pageable pageable) {
		// asset.setFtpFolder(this.getAssetFolder(asset.getAssetNo()));
		Page<Asset> assets = null;
		if (queryParams == null) {
			assets = this.assetRepo.findAll(pageable);
		} else {
			assets = this.assetRepo.findAll(this.query(queryParams), pageable);
		}

		for (Asset asset : assets) {
			asset.setFtpFolder(this.getAssetFolder(asset.getAssetNo()));
			asset.setFullFtpFolder("ftp://" + this.ftpProperties.getFtpServiceUrl() + asset.getFtpFolder());
		}
		return assets;
	}

	@Override
	public Asset getSimpleAsset(String assetNo) throws BusinessException {
		Asset asset = this.assetRepo.getAsset(assetNo);
		if (asset == null) {
			throw new BusinessException(ErrorCode.ASSET_NOT_FOUND);
		}
		return asset;
	}

	@Override
	public Asset getAssetWithFiles(String assetNo) throws BusinessException {
		Asset asset = this.getSimpleAsset(assetNo);
		asset.setAssetFiles(this.getAssetFiles(asset));
		asset.setViewTags(this.getAssetViewTags(asset.getId()));
		return asset;
	}

	@Override
	public Asset getAssetDetail(String assetNo) throws BusinessException {
		Asset asset = this.assetRepo.getAsset(assetNo);
		if (asset == null) {
			throw new BusinessException(ErrorCode.ASSET_NOT_FOUND);
		}
		asset.setWeatherTag(this.assetTagService.getAssetTag(asset.getWeatherTagId()));
		asset.setRoadTag(this.assetTagService.getAssetTag(asset.getRoadTagId()));
		// for (Asset2AssetViewTag viewTag : asset.getViewTags()) {
		// viewTag.setAssetId(asset.getId());
		// this.asset2AssetViewTagRepo.save(viewTag);
		// }
		asset.setViewTags(this.getAssetViewTags(asset.getId()));
		asset.setAssetFiles(this.getAssetFiles(asset));
		asset.setFtpFolder(this.getAssetFolder(asset.getAssetNo()));
		asset.setFullFtpFolder("ftp://" + this.ftpProperties.getFtpServiceUrl() + asset.getFtpFolder());
		return asset;
	}

	@Override
	public List<Asset2AssetViewTag> getAssetViewTags(Long assetId) {
		List<Asset2AssetViewTag> tags = this.asset2AssetViewTagRepo.getListByAssetId(assetId);
		for (Asset2AssetViewTag tag : tags) {
			tag.setViewTag(assetViewTagService.getAssetViewTag(tag.getAssetViewTagId()));
		}
		return tags;
	}

	private List<AssetFile> getAssetFiles(Asset asset) {
		return this.assetFileRepo.getAssetFiles(asset.getAssetNo());
	}

	@Override
	public void setStatus(String assetNo, Status status) throws BusinessException {
		Asset asset = this.getAssetByAssetNo(assetNo);
		asset.setStatus(status);
		this.assetRepo.save(asset);
	}

	@Override
	public void update(Asset asset) {
		this.assetRepo.save(asset);
	}

	private Asset getAssetByAssetNo(String assetNo) throws BusinessException {
		Asset asset = this.assetRepo.getAsset(assetNo);
		if (asset == null) {
			throw new BusinessException(ErrorCode.ASSET_NOT_FOUND);
		}

		return asset;
	}

	@Transactional
	@Override
	public void setAssetTags(String assetNo, AssetTagParam tagParam) throws BusinessException {
		Asset asset = this.getAssetByAssetNo(assetNo);
		AssetTag weatherTag = this.assetTagService.getAssetTag(tagParam.getWeatherTagId());
		AssetTag roadTag = this.assetTagService.getAssetTag(tagParam.getRoadTagId());
		if (weatherTag == null || roadTag == null) {
			throw new BusinessException(ErrorCode.ASSET_TAG_UPDATE_FAILED);
		}
		asset.setRoadTagId(tagParam.getRoadTagId());
		asset.setWeatherTagId(tagParam.getWeatherTagId());
		this.taskService.updateAssetTags(assetNo, tagParam.getRoadTagId(), tagParam.getWeatherTagId());

		this.assetRepo.save(asset);
	}

	private Specification<Asset> query(final QueryParams queryParams) {
		return new Specification<Asset>() {
			@Override
			public Predicate toPredicate(Root<Asset> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> whereList = new ArrayList<>();
				for (Filter filter : queryParams.getFilters()) {
					Predicate predicate = filter.toPredicate(root, query, cb);
					if (predicate != null) {
						whereList.add(predicate);
					}
				}

				if (whereList.size() != 0) {
					Predicate[] predicates = new Predicate[whereList.size()];
					query = query.where(whereList.toArray(predicates));
				}

				List<Order> orderList = new ArrayList<>();
				for (OrderBy orderBy : queryParams.getOrders()) {
					Order order = orderBy.toOrder(root, cb);
					if (order != null) {
						orderList.add(order);
					}
				}

				if (orderList.size() != 0) {
					Order[] orders = new Order[orderList.size()];
					query = query.orderBy(orderList.toArray(orders));
				}

				return query.getRestriction();
			}
		};
	}

	@Override
	public Page<Asset> queryAssets(AssetSearchParam assetSearchParam, Pageable pageable) {
		AssetSpecification assetSpecification = new AssetSpecification();
		Page<Asset> assets = this.assetRepo.findAll(assetSpecification.search(assetSearchParam), pageable);
		for (Asset asset : assets) {
			asset.setFtpFolder(this.getAssetFolder(asset.getAssetNo()));
			asset.setFullFtpFolder("ftp://" + this.ftpProperties.getFtpServiceUrl() + asset.getFtpFolder());
			if (asset.getAssetType() == AssetType.PICTURE) {
				asset.setPictureCount(this.assetFileRepo.getAssetFilesCount(asset.getAssetNo()));
			}
		}

		return assets;
	}

	@Override
	public List<AssetFile> getPendingUploadVideoFiles(int count) {
		return this.assetFileRepo.getPendingUploadVideoFiles(count);
	}

	@Override
	public void setAssetFileStatus(AssetFile assetFile, AssetFileStatus assetFileStatus) {
		assetFile.setAssetFileStatus(assetFileStatus);
		this.assetFileRepo.save(assetFile);

	}

}

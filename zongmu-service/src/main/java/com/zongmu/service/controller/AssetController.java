package com.zongmu.service.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.zongmu.service.criteria.AssetContainsAssetTags;
import com.zongmu.service.criteria.Equal;
import com.zongmu.service.criteria.GreaterThan;
import com.zongmu.service.criteria.LessThan;
import com.zongmu.service.criteria.QueryParams;
import com.zongmu.service.dto.asset.AssetFileStatus;
import com.zongmu.service.dto.asset.AssetTagParam;
import com.zongmu.service.dto.asset.AssetType;
import com.zongmu.service.dto.asset.AssetViewTagParam;
import com.zongmu.service.dto.asset.FileData;
import com.zongmu.service.dto.asset.PicData;
import com.zongmu.service.dto.search.AssetSearchParam;
import com.zongmu.service.entity.Asset;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.AssetService;

@Controller
@RequestMapping("/assets")
public class AssetController {

	private static Logger logger = Logger.getLogger(AssetController.class);

	@Autowired
	private AssetService assetService;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Asset createAsset(@RequestBody Asset asset) throws BusinessException {
		logger.info("Creat asset ...");
		return this.assetService.createAsset(asset);
	}
	
	@RequestMapping(value = "/queryAssets", method = RequestMethod.POST)
	@ResponseBody
	public Page<Asset> queryAssets(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestBody AssetSearchParam assetSearchParam) {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.assetService.queryAssets(assetSearchParam, pageable);
	}

	@RequestMapping(value = "/{assetNo}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteAsset(@PathVariable("assetNo") String assetNo) throws BusinessException {
		this.assetService.deleteAsset(assetNo);
	}

	@RequestMapping(value = "/{assetNo}/upload", method = RequestMethod.POST)
	@ResponseBody
	public void uploadFile(@PathVariable("assetNo") String assetNo, @RequestParam("file") MultipartFile file)
			throws BusinessException {
		this.assetService.attachFileToAsset(assetNo, file);
	}

	@RequestMapping(value = "/{assetNo}/setUploadFileData", method = RequestMethod.POST)
	@ResponseBody
	public void setUploadFileData(@PathVariable("assetNo") String assetNo, @RequestBody FileData fileData)
			throws BusinessException {
		this.assetService.setUploadFileData(assetNo, fileData);
	}

	@RequestMapping(value = "/{assetNo}/setFourUploadFileData", method = RequestMethod.POST)
	@ResponseBody
	public void setFourUploadFileData(@PathVariable("assetNo") String assetNo, @RequestBody FileData[] fileDatas)
			throws BusinessException {
		this.assetService.setFourUploadFileData(assetNo, fileDatas);
	}
	
	@RequestMapping(value = "/{assetNo}/setUploadPicFileDatas", method = RequestMethod.POST)
	@ResponseBody
	public void setFourUploadFileData(@PathVariable("assetNo") String assetNo, @RequestBody PicData[] picDatas)
			throws BusinessException {
		this.assetService.setUploadPicData(assetNo, picDatas);
	}

	@RequestMapping(value = "/{assetNo}/updateTags", method = RequestMethod.POST)
	@ResponseBody
	public void setAssetTags(@PathVariable("assetNo") String assetNo, @RequestBody AssetTagParam tagParam)
			throws BusinessException {
		this.assetService.setAssetTags(assetNo, tagParam);
	}

	@RequestMapping(value = "/{assetNo}/updateAssetViewTags", method = RequestMethod.POST)
	@ResponseBody
	public void updateAssetViewTags(@PathVariable("assetNo") String assetNo, @RequestBody AssetViewTagParam tagParam)
			throws BusinessException {
		this.assetService.updateAssetViewTags(assetNo, tagParam);
	}

	@RequestMapping(value = "/{assetFileNo}/uploadagain", method = RequestMethod.POST)
	@ResponseBody
	public void uploadFileAgain(@PathVariable("assetNo") String assetNo, @RequestParam("file") MultipartFile file) {

	}

	@RequestMapping(value = "/{assetNo}/compress", method = RequestMethod.POST)
	@ResponseBody
	public void compress(@PathVariable("assetNo") String assetNo) throws BusinessException {
		this.assetService.compress(assetNo);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Page<Asset> getAssets(@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestParam(value = "filter", required = false) String filter) {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		QueryParams params = null;
		if (!StringUtils.isEmpty(filter)) {
			params = new QueryParams();
			String[] wheres = filter.split(";");
			for (String where : wheres) {
				if (where.contains(" eq ") || where.contains(" = ")) {
					Equal equal = this.parseToEqual(where);
					if (equal != null) {
						params.add(equal);
					}
				} else if (where.contains(" > ")) {
					GreaterThan gt = this.parseToGreaterThan(where);
					if (gt != null) {
						params.add(gt);
					}
				} else if (where.contains(" < ")) {
					LessThan lt = this.parseToLessThan(where);
					if (lt != null) {
						params.add(lt);
					}
				} else if (where.contains(" in ")) {
					String[] parts = where.split(" ");
					if (StringUtils.equalsIgnoreCase("tags", parts[0])) {
						params.add(new AssetContainsAssetTags(parts[2]));
					}
				}
			}
		}

		Page<Asset> assets = this.assetService.getAssets(params, pageable);
		return assets;
	}

	@RequestMapping(value = "/{assetNo}", method = RequestMethod.GET)
	@ResponseBody
	public Asset getAsset(@PathVariable("assetNo") String assetNo) throws BusinessException {
		return this.assetService.getAssetDetail(assetNo);
	}

	@RequestMapping(value = "/{assetFileNo}/compressSuccess", method = RequestMethod.POST)
	@ResponseBody
	public void compressSuccess(@PathVariable("assetFileNo") String assetFileNo) throws BusinessException {
		this.assetService.updateStatus(assetFileNo, AssetFileStatus.COMPRESSSUCCESS);
	}

	@RequestMapping(value = "/{assetFileNo}/compressFailure", method = RequestMethod.POST)
	@ResponseBody
	public void compressFailed(@PathVariable("assetFileNo") String assetFileNo) throws BusinessException {
		this.assetService.updateStatus(assetFileNo, AssetFileStatus.COMPRESSFAILED);
	}

	private Equal parseToEqual(String expr) {
		String[] parts = expr.split(" ");
		if (StringUtils.equalsIgnoreCase(parts[0], "assetType")) {
			return new Equal("assetType", AssetType.valueOf(parts[2]), AssetType.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "uploadTime")) {
			return new Equal("uploadTime", DateTime.parse(parts[2]), DateTime.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "recordTime")) {
			return new Equal("recordTime", DateTime.parse(parts[2]), DateTime.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "recordLength")) {
			return new Equal("recordLength", Float.parseFloat(parts[2]), Float.class);
		}

		return null;
	}

	private GreaterThan parseToGreaterThan(String expr) {
		String[] parts = expr.split(" ");
		if (StringUtils.equalsIgnoreCase(parts[0], "uploadTime")) {
			return new GreaterThan("uploadTime", DateTime.parse(parts[2]), DateTime.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "recordTime")) {
			return new GreaterThan("recordTime", DateTime.parse(parts[2]), DateTime.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "recordLength")) {
			return new GreaterThan("recordLength", Float.parseFloat(parts[2]), Float.class);
		}

		return null;
	}

	private LessThan parseToLessThan(String expr) {
		String[] parts = expr.split(" ");
		if (StringUtils.equalsIgnoreCase(parts[0], "uploadTime")) {
			return new LessThan("uploadTime", DateTime.parse(parts[2]), DateTime.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "recordTime")) {
			return new LessThan("recordTime", DateTime.parse(parts[2]), DateTime.class);
		} else if (StringUtils.equalsIgnoreCase(parts[0], "recordLength")) {
			return new LessThan("recordLength", Float.parseFloat(parts[2]), Float.class);
		}

		return null;
	}
}

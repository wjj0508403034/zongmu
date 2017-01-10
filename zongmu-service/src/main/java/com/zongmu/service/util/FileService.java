package com.zongmu.service.util;

import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.zongmu.service.configuration.ApplicationProperties;
import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.Task;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.FtpService;
import com.zongmu.service.properties.FtpProperties;

@Service
public class FileService {

	private static Logger logger = Logger.getLogger(FileService.class);

	@Autowired
	private ApplicationProperties applicationProperties;

	@Autowired
	private CommonService commonService;

	@Autowired
	private FtpService ftpService;
	
	@Autowired
	private FtpProperties ftpProperties;
	
	public String getFTPPath(Task task){
		return this.ftpProperties.getFtpServiceUrl() + "/upload/" + task.getAssetNo() + "/Datalog/compress/" + task.getTaskNo() + "/";
	}

	public File getFile(AssetFile assetFile) {
		String path = this.applicationProperties.getUploadFolder() + "/" + assetFile.getAssetNo() + "/"
				+ assetFile.getFileName();
		return new File(path);
	}

	public boolean assetFileExists(AssetFile assetFile) {
		return this.getFile(assetFile).exists();
	}

	public boolean deleteFile(AssetFile assetFile) {
		File file = this.getFile(assetFile);
		logger.info("Start to delete file: " + file.getPath());
		if (file.exists()) {
			if (file.delete()) {
				logger.info("File delete success: " + file.getPath());
			} else {
				logger.warn("File delete failed: " + file.getPath());
			}
		} else {
			logger.warn("File not exist: " + file.getPath());
		}
		return false;
	}

	public void saveXmlFile2(Document doc, String assetNo, String fileName) {
		try {
			String xmlDir = this.applicationProperties.getUploadFolder() + "/xml";
			String filePath = xmlDir + "/" + fileName;
			logger.info("Save xml file " + filePath);
			boolean created = this.retryCreateFolder(xmlDir);
			if (created) {
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(filePath));
				transformer.transform(source, result);
				logger.info("Upload xml file to ftp ...");
				boolean uploadResult = this.ftpService.uploadXml2(new File(filePath), assetNo, fileName);
				if (uploadResult) {
					logger.info("Upload xml file to ftp success");
				} else {
					logger.error("Upload xml file to ftp failed");
				}

			} else {
				logger.error("Create folder failed" + filePath);
			}
		} catch (Exception ex) {
			logger.error("Save xml file failed");
			logger.error(ExceptionUtils.getStackTrace(ex));
		}
	}

	public void saveXmlFile(Document doc, Task task, String fileName) {
		try {
			String xmlDir = this.applicationProperties.getUploadFolder() + "/xml";
			String filePath = xmlDir + "/" + fileName;
			logger.info("Save xml file " + filePath);
			boolean created = this.retryCreateFolder(xmlDir);
			if (created) {
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(filePath));
				transformer.transform(source, result);
				logger.info("Upload xml file to ftp ...");
				boolean uploadResult = this.ftpService.uploadXml(new File(filePath), task);
				if (uploadResult) {
					logger.info("Upload xml file to ftp success");
				} else {
					logger.error("Upload xml file to ftp failed");
				}

			} else {
				logger.error("Create folder failed" + filePath);
			}
		} catch (Exception ex) {
			logger.error("Save xml file failed");
			logger.error(ExceptionUtils.getStackTrace(ex));
		}
	}

	private boolean retryCreateFolder(String folder) throws InterruptedException {
		boolean created = true;
		int count = 0;
		while (count <= 3) {
			if (!this.exist(folder)) {
				created = this.safeCreateFolder(folder);
			}
			if (created) {
				break;
			}
			count++;
			Thread.sleep(1000);
		}

		return created;
	}

	public String saveUserIcon(MultipartFile file) throws BusinessException {

		if (!this.exist(this.applicationProperties.getUploadFolder())) {
			if (!this.safeCreateFolder(this.applicationProperties.getUploadFolder())) {
				throw new BusinessException(ErrorCode.UPLOAD_USER_ICON_FAILED);
			}
		}

		String dir = this.applicationProperties.getUploadFolder() + "/users";
		if (!this.safeCreateFolder(dir)) {
			throw new BusinessException(ErrorCode.UPLOAD_USER_ICON_FAILED);
		}

		String fileName = this.commonService.generateNo();
		File temp = new File(dir + "/" + fileName);

		try {
			file.transferTo(temp);
			return fileName;
		} catch (Exception ex) {
			logger.error("Save file " + file.getOriginalFilename() + "failed");
			logger.error(ExceptionUtils.getStackTrace(ex));
			throw new BusinessException(ErrorCode.UPLOAD_USER_ICON_FAILED);
		}
	}

	public boolean saveFile(AssetFile assetFile, MultipartFile file) {
		boolean created = true;
		if (!this.exist(this.applicationProperties.getUploadFolder())) {
			created = this.safeCreateFolder(this.applicationProperties.getUploadFolder() + "/");
		}

		String dir = this.applicationProperties.getUploadFolder() + "/" + assetFile.getAssetNo() + "/";
		if (created) {
			created = this.safeCreateFolder(dir);
			if (created) {

				File temp = new File(dir + "/" + file.getOriginalFilename());
				try {
					file.transferTo(temp);
					
					return true;
				} catch (Exception ex) {
					logger.error("Save file " + file.getOriginalFilename() + "failed");
					logger.error(ExceptionUtils.getStackTrace(ex));
				}

			} else {
				logger.error("Create folder " + dir + "failed");
			}

		} else {
			logger.error("Create folder " + dir + "failed");
		}

		return false;
	}

	private synchronized boolean safeCreateFolder(String path) {
		// Files.createDirectory(Paths.get(path), attrs)
		File target = new File(path);
		if (!target.exists()) {
			try {
				boolean canWrite = target.canWrite();
				logger.info("Path:" + path + "can write " + canWrite);
				boolean result = target.mkdir();
				if (result) {
					logger.info("Create folder success:" + path);
				} else {
					logger.error("Create folder failed:" + path);
				}
			} catch (Exception ex) {
				logger.error("Create folder failed.", ex);
				logger.error(ExceptionUtils.getStackTrace(ex));
				return false;
			}
		}

		return true;
	}

	private boolean exist(String path) {
		File file = new File(path);
		return file.exists();
	}

}

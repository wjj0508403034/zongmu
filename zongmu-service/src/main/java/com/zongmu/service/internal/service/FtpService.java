package com.zongmu.service.internal.service;

import java.io.File;

import com.zongmu.service.entity.AssetFile;
import com.zongmu.service.entity.Task;
import com.zongmu.service.exception.BusinessException;

public interface FtpService {

	void upload(AssetFile assetFile) throws BusinessException;

	boolean uploadXml(File file, Task task);

	boolean uploadXml2(File file, String assetNo, String fileName);

	void uploadPic(AssetFile assetFile);
}

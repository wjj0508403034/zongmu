package com.zongmu.service.internal.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zongmu.service.dto.search.ReportSearchParam;
import com.zongmu.service.dto.tag.TagCategory;
import com.zongmu.service.entity.AssetTag;
import com.zongmu.service.entity.ViewTag;
import com.zongmu.service.entity.ViewTagItem;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.AssetTagService;
import com.zongmu.service.internal.service.ReportService;
import com.zongmu.service.internal.service.TaskService;
import com.zongmu.service.internal.service.ViewTagService;
import com.zongmu.service.report.bsd.Header;
import com.zongmu.service.report.bsd.Report;
import com.zongmu.service.report.bsd.RowData;
import com.zongmu.service.report.bsd.Table;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	private TaskService taskService;

	@Autowired
	private AssetTagService assetTagService;

	@Autowired
	private ViewTagService viewTagService;

	private List<Header> getHeaders(List<AssetTag> tags) {
		List<Header> headers = new ArrayList<>();
		for (AssetTag tag : tags) {
			headers.add(new Header(tag.getId().toString(), tag.getName()));
		}

		return headers;
	}

	private Table getTable(Long algorithmId, List<Long> assetViewItemIds, ViewTag viewTag) {
		Table table = new Table();
		table.setName(viewTag.getName());
		RowData videoRowData = new RowData();
		videoRowData.setText("视频");
		RowData picRowData = new RowData();
		picRowData.setText("图片");
		List<Header> headers = new ArrayList<>();
		for (ViewTagItem tagItem : viewTag.getItems()) {
			headers.add(new Header(tagItem.getId().toString(), tagItem.getName()));
			Long sumVideo = this.taskService.newSumVideo(algorithmId, assetViewItemIds, tagItem.getId());
			if (sumVideo == null) {
				sumVideo = 0l;
			}
			videoRowData.getData().put(tagItem.getId().toString(), sumVideo);
			Long countPic = this.taskService.newCountPic(algorithmId, assetViewItemIds, tagItem.getId());
			picRowData.getData().put(tagItem.getId().toString(), countPic);
		}
		headers.add(new Header("__subTotal", "总计"));
		table.setHeaders(headers);
		table.addRowData(videoRowData);
		table.addRowData(picRowData);
		return table;
	}

	@Override
	public Report getNewReport(Long algorithmId, List<Long> assetViewItemIds) {
		Report report = new Report();
		List<ViewTag> viewTags = this.viewTagService.getViewTags(algorithmId);
		for (ViewTag tag : viewTags) {
			report.getTables().add(this.getTable(algorithmId, assetViewItemIds, tag));
		}
		return report;
	}

	@Override
	public Report search(Long algorithmId, ReportSearchParam reportSearchParam) {
		Report report = new Report();
		List<ViewTag> viewTags = this.viewTagService.getViewTags(algorithmId);
		for (ViewTag tag : viewTags) {
			report.getTables().add(this.getTable(algorithmId, reportSearchParam, tag));
		}
		return report;
	}

	private Table getTable(Long algorithmId, ReportSearchParam reportSearchParam, ViewTag viewTag) {
		reportSearchParam.setAlgorithmId(algorithmId);
		Table table = new Table();
		table.setName(viewTag.getName());
		RowData videoRowData = new RowData();
		videoRowData.setText("视频");
		RowData picRowData = new RowData();
		picRowData.setText("图片");
		List<Header> headers = new ArrayList<>();
		Long videoTotal = 0l;
		Long pictureTotal = 0l;
		for (ViewTagItem tagItem : viewTag.getItems()) {
			headers.add(new Header(tagItem.getId().toString(), tagItem.getName()));
			Long sumVideo = this.taskService.calcVideoTotalLength(algorithmId, tagItem.getId(), reportSearchParam);
			if (sumVideo == null) {
				sumVideo = 0l;
			}
			videoRowData.getData().put(tagItem.getId().toString(), sumVideo);
			Long countPic = this.taskService.calcPictureTotalCount(algorithmId, tagItem.getId(), reportSearchParam);
			picRowData.getData().put(tagItem.getId().toString(), countPic);
			videoTotal += sumVideo;
			pictureTotal += countPic;
		}
		headers.add(new Header("__subTotal", "总计"));
		table.setHeaders(headers);
		videoRowData.getData().put("__subTotal", videoTotal);
		picRowData.getData().put("__subTotal", pictureTotal);
		table.addRowData(videoRowData);
		table.addRowData(picRowData);
		return table;
	}

	@Override
	public Report getReport(Long algorithmId, int from, int to) throws BusinessException {
		Report report = new Report();

		List<AssetTag> assetTags = this.assetTagService.getAssetTags();
		List<AssetTag> roadTags = new ArrayList<>();
		List<AssetTag> weatherTags = new ArrayList<>();
		for (AssetTag assetTag : assetTags) {
			if (assetTag.getCategory() == TagCategory.ROAD) {
				roadTags.add(assetTag);
			} else if (assetTag.getCategory() == TagCategory.WEATHER) {
				weatherTags.add(assetTag);
			}
		}

		List<Header> headers = this.getHeaders(weatherTags);
		report.getPictureTable().setHeaders(headers);
		report.getVideoTable().setHeaders(headers);

		for (AssetTag roadTag : roadTags) {
			RowData videoRowData = new RowData();
			videoRowData.setText(roadTag.getName());
			RowData picRowData = new RowData();
			picRowData.setText(roadTag.getName());
			for (AssetTag weatherTag : weatherTags) {
				Long sumVideoLength = this.taskService.sumVideoLength(algorithmId, roadTag.getId(), weatherTag.getId(),
						from, to);
				videoRowData.getData().put(weatherTag.getId().toString(), sumVideoLength);
				Long countPic = this.taskService.countPicCount(algorithmId, roadTag.getId(), weatherTag.getId(), from,
						to);
				picRowData.getData().put(weatherTag.getId().toString(), countPic);
			}
			report.getPictureTable().addRowData(picRowData);
			report.getVideoTable().addRowData(videoRowData);

		}
		return report;
	}

}

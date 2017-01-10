package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.dto.search.ReportSearchParam;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.report.bsd.Report;

public interface ReportService {

	Report getReport(Long algorithmId,int from, int to) throws BusinessException;

	Report getNewReport(Long algorithmId, List<Long> assetViewItemIds);

	Report search(Long algorithmId, ReportSearchParam reportSearchParam);
}

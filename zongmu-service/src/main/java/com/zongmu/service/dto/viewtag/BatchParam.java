package com.zongmu.service.dto.viewtag;

import java.util.ArrayList;
import java.util.List;

public class BatchParam {

	private Long algorithmId;
	private List<String> names = new ArrayList<>();

	public Long getAlgorithmId() {
		return algorithmId;
	}

	public void setAlgorithmId(Long algorithmId) {
		this.algorithmId = algorithmId;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}
}

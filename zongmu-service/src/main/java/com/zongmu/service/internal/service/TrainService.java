package com.zongmu.service.internal.service;

import java.util.List;

import com.zongmu.service.entity.Train;
import com.zongmu.service.exception.BusinessException;

public interface TrainService {

	List<Train> getTrains();
	
	void create(Train train) throws BusinessException;
	
	void delete(Long id);
	
	Train get(Long id);

	void update(Long trainId, Train train) throws BusinessException;
}

package com.zongmu.service.internal.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zongmu.service.entity.Train;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.TrainService;
import com.zongmu.service.repository.TrainRepository;

@Service
public class TrainServiceImpl implements TrainService {

	@Autowired
	private TrainRepository trainRepo;

	@Override
	public List<Train> getTrains() {
		List<Train> trains = new ArrayList<>();
		for (Train train : this.trainRepo.findAll()) {
			train.setBody(null);
			trains.add(train);
		}
		return trains;
	}

	@Override
	public void create(Train train) throws BusinessException {
		if(this.trainRepo.existsCheckBeforeCreate(train.getSubject())){
			throw new BusinessException(ErrorCode.Train_Name_Dup);
		}
		this.trainRepo.save(train);
	}

	@Override
	public void delete(Long id) {
		this.trainRepo.delete(id);
	}

	@Override
	public Train get(Long id) {
		return this.trainRepo.findOne(id);
	}

	@Override
	public void update(Long trainId, Train train) throws BusinessException {
		Train oldTrain = this.get(trainId);
		if (oldTrain == null) {
			throw new BusinessException(ErrorCode.TRAIN_NOT_EXIST);
		}
		
		if(this.trainRepo.existsCheckBeforeUpdate(train.getSubject(),trainId)){
			throw new BusinessException(ErrorCode.Train_Name_Dup);
		}

		oldTrain.setSubject(train.getSubject());
		oldTrain.setBody(train.getBody());
		this.trainRepo.save(oldTrain);
	}

}

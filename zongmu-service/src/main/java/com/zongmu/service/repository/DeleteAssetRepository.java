package com.zongmu.service.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.DeleteAsset;

@Repository
public interface DeleteAssetRepository  extends PagingAndSortingRepository<DeleteAsset, Long>{

}

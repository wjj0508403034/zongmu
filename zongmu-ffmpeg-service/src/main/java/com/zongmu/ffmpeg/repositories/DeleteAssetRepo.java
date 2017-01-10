package com.zongmu.ffmpeg.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.ffmpeg.entities.DeleteAsset;

@Repository
public interface DeleteAssetRepo extends PagingAndSortingRepository<DeleteAsset, Long>  {

    @Query(value = "select * from DeleteAsset where status = 0 LIMIT ?", nativeQuery = true)
    List<DeleteAsset> getDeleteAssets(int count);
}

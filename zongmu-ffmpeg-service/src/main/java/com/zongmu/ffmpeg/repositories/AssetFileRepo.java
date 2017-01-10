package com.zongmu.ffmpeg.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.ffmpeg.entities.AssetFile;

@Repository
public interface AssetFileRepo extends PagingAndSortingRepository<AssetFile, Long> {

    @Query("select t from AssetFile t where t.assetNo = ?1")
    List<AssetFile> getAssetFiles(String assetNo);
    
    @Query(value = "select * from AssetFile where assetFileStatus = 5 and assetType <> 2 LIMIT ?", nativeQuery = true)
    List<AssetFile> getPendingAssetFile(int count);
    
    @Query(value = "select * from AssetFile where assetFileStatus = 7 and assetType <> 2 LIMIT ?", nativeQuery = true)
    List<AssetFile> getCompressFailedAssetFile(int count);
}

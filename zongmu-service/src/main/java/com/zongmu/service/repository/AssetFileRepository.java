package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.dto.asset.AssetFileStatus;
import com.zongmu.service.entity.AssetFile;

@Repository
public interface AssetFileRepository extends PagingAndSortingRepository<AssetFile, Long> {

	@Query("select t from AssetFile t where t.assetNo = ?1")
	List<AssetFile> getAssetFiles(String assetNo);
	
	@Query("select count(t) from AssetFile t where t.assetNo = ?1")
	Long getAssetFilesCount(String assetNo);

	@Query("select t from AssetFile t where t.assetNo= ?1 and t.fileName = ?2")
	AssetFile getAssetFile(String assetNo, String fileName);

	@Query("select t from AssetFile t where t.assetFileNo = ?1")
	AssetFile getAssetFileByAssetFileNo(String assetFileNo);
	
	@Query("select t from AssetFile t where t.assetFileStatus = ?1")
	List<AssetFile> getFtpUploadFailedFiles(AssetFileStatus status);
	
	@Modifying
	@Query("delete from AssetFile t where t.assetNo = ?1")
	void deleteAssetFiles(String assetNo);
	
	 @Query(value = "select * from AssetFile where assetType != 2 and assetFileStatus = 2 LIMIT ?", nativeQuery = true)
	List<AssetFile> getPendingUploadVideoFiles(int count);
}

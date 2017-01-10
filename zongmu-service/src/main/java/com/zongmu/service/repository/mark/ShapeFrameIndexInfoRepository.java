package com.zongmu.service.repository.mark;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.mark.ShapeFrameIndexInfo;

@Repository
public interface ShapeFrameIndexInfoRepository extends CrudRepository<ShapeFrameIndexInfo, Long>{

	@Query("select t from ShapeFrameIndexInfo t where t.taskMarkRecordId = ?1")
	List<ShapeFrameIndexInfo> query(Long taskMarkRecordId);

}

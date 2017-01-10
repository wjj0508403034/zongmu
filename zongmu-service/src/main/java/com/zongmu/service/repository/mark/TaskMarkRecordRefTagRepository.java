package com.zongmu.service.repository.mark;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.zongmu.service.entity.mark.TaskMarkRecordRefTag;

@Repository
public interface TaskMarkRecordRefTagRepository extends CrudRepository<TaskMarkRecordRefTag, Long>{

	@Query("select t from TaskMarkRecordRefTag t where t.taskMarkRecordId = ?1")
	List<TaskMarkRecordRefTag> getRefTags(Long taskMarkRecordId);

	@Transactional
	@Modifying
	@Query("delete from TaskMarkRecordRefTag t where t.taskMarkRecordId = ?1")
	void deleteRefTags(Long taskMarkRecordId);
	
	@Query("select count(t) from TaskMarkRecordRefTag t where t.tagItemId = ?1")
	Long countByTagItem(Long tagItemId);
	
	@Query("select count(t) > 0 from TaskMarkRecordRefTag t where t.tagItemId in (?1)")
	boolean isTagUsing(List<Long> tagItemIds);
	
	
	@Query("select count(t) > 0 from TaskMarkRecordRefTag t where t.tagItemId in (select tagItem.id from TagItem tagItem where tagItem.tagId = ?1)")
	boolean checkBeforeDelete(Long tagId);
}

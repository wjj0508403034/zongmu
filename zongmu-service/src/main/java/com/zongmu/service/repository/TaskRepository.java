package com.zongmu.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.zongmu.service.entity.Task;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<Task, Long>, JpaSpecificationExecutor<Task> {
	
	@Query("select t from Task t where t.taskNo = ?1")
	Task getTask(String taskNo);
	
	@Query("select count(t) > 0 from Task t where t.taskName = ?1")
	boolean checkTaskDupBeforeCreate(String name);

	@Query("select t from Task t where t.assetNo = ?1 order by t.createTime desc")
	List<Task> findTasks(String assetNo);

	@Query("select task from Task task, " + "TaskItem taskItem "
			+ "where taskItem.taskRecordNo = ?1 and task.id = taskItem.taskId")
	Task getTaskByTaskRecordNo(String taskRecordNo);

	@Query("select count(t) from Task t where t.algorithmId = ?1")
	Long countTasksByAlgorithm(Long algorithmId);

	@Modifying
	@Query("delete from Task t where t.assetNo = ?1")
	void deleteTaskByAssetNo(String assetNo);

	@Modifying
	@Query("update Task t set t.roadTagId = ?2 , t.weatherTagId = ?3 where t.assetNo = ?1")
	void updateTags(String assetNo, Long roadTagId, Long weatherTagId);

	@Query("select sum(task.videoLength) from Task task, TaskItem taskItem "
			+ "where task.algorithmId = ?1 and task.roadTagId = ?2 and task.weatherTagId= ?3 and task.taskType = 0 "
			+ "and taskItem.taskId = task.id and task.timeOfDay >= ?4 and task.timeOfDay < ?5")
	Long sum(Long algorithmId, Long roadTagId, Long weatherTagId, int from, int to);

	/*
	 * 临时的query，这个有问题 select sum(taskItem.videoLength) from TaskItem taskItem
	 * where taskItem.taskType = 0 and taskItem.algorithmId = ?1 and
	 * taskItem.taskId in (select viewTag.taskId from TaskXViewTag viewTag where
	 * viewTag.viewTagItemId = ?2)
	 */
	// @Query("select sum(task.videoLength) from Task task where task.taskType =
	// 0 and task.algorithmId = ?1 "
	// + "and task.id in (select viewTag.taskId from TaskXViewTag viewTag where
	// viewTag.viewTagItemId = ?2)")
	@Query("select sum(taskItem.videoLength) from TaskItem taskItem where taskItem.taskType = 0 and taskItem.algorithmId = ?1"
			+ " and taskItem.id in (select viewTag.taskItemId from TaskItemXViewTag viewTag where viewTag.viewTagItemId = ?2)")
	Long newSumVideo(Long algorithmId, Long viewTagItemId);

	/*
	 * 临时的query，这个有问题 select sum(taskItem.videoLength) from TaskItem taskItem
	 * where taskItem.taskType = 0 and taskItem.algorithmId = ?1 and taskItem.id
	 * in (select assetViewTag.taskId from Task2AssetViewTag assetViewTag where
	 * assetViewTag.assetViewTagItemId in (?2)) and taskItem.taskId in (select
	 * viewTag.taskId from TaskXViewTag viewTag where viewTag.viewTagItemId =
	 * ?3)
	 * 
	 * @Query(
	 * "select sum(task.videoLength) from Task task where task.taskType = 0 and task.algorithmId = ?1 "
	 * +
	 * "and task.id in (select assetViewTag.taskId from Task2AssetViewTag assetViewTag where assetViewTag.assetViewTagItemId in (?2))"
	 * +
	 * "and task.id in (select viewTag.taskId from TaskXViewTag viewTag where viewTag.viewTagItemId = ?3)"
	 * )
	 * 
	 */
	@Query("select sum(taskItem.videoLength) from TaskItem taskItem where taskItem.taskType = 0 and taskItem.algorithmId = ?1 "
			+ "and taskItem.taskId in (select assetViewTag.taskId from Task2AssetViewTag assetViewTag where assetViewTag.assetViewTagItemId in (?2)) "
			+ "and taskItem.id in (select viewTag.taskItemId from TaskItemXViewTag viewTag where viewTag.viewTagItemId = ?3)")
	Long newSumVideo(Long algorithmId, List<Long> assetViewTagItemIds, Long viewTagItemId);

	@Query("select count(taskItem) from Task task, TaskItem taskItem "
			+ "where task.algorithmId = ?1 and task.roadTagId = ?2 and task.weatherTagId= ?3 and task.taskType = 1 "
			+ "and taskItem.taskId = task.id and task.timeOfDay >= ?4 and task.timeOfDay < ?5")
	Long count(Long algorithmId, Long roadTagId, Long weatherTagId, int from, int to);

	@Query("select count(taskItem) from TaskItem taskItem where taskItem.taskType = 1 and taskItem.algorithmId = ?1 "
			+ "and taskItem.taskId in (select assetViewTag.taskId from Task2AssetViewTag assetViewTag where assetViewTag.assetViewTagItemId in (?2)) "
			+ "and taskItem.id in (select viewTag.taskItemId from TaskItemXViewTag viewTag where viewTag.viewTagItemId = ?3)")
	Long newCountPicture(Long algorithmId, List<Long> assetViewTagItemIds, Long viewTagItemId);

	@Query("select count(taskItem) from TaskItem taskItem where taskItem.taskType = 1 and taskItem.algorithmId = ?1 "
			+ "and taskItem.id in (select viewTag.taskItemId from TaskItemXViewTag viewTag where viewTag.viewTagItemId = ?2)")
	Long newCountPicture(Long algorithmId, Long viewTagItemId);
}

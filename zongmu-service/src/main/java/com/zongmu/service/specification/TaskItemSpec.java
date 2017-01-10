package com.zongmu.service.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.util.StringUtils;

import com.zongmu.service.dto.reviewrecord.ReviewRecordStatus;
import com.zongmu.service.dto.search.DateRange;
import com.zongmu.service.entity.ReviewRecord;
import com.zongmu.service.entity.TaskItem;
import com.zongmu.service.entity.TaskItemXViewTag;

public class TaskItemSpec {

	public static Predicate taskNamePredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String taskName) {
		if (StringUtils.isEmpty(taskName)) {
			return null;
		}

		Subquery<String> taskItemQuery = query.subquery(String.class);
		Root<TaskItem> taskItemRoot = taskItemQuery.from(TaskItem.class);
		taskItemQuery.select(taskItemRoot.get("taskItemNo").as(String.class));

		Predicate like = cb.like(taskItemRoot.get("taskName").as(String.class), "%" + taskName + "%");
		return root.get("taskItemNo").as(String.class).in(taskItemQuery.where(like));
	}

	public static Predicate taskItemNoPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			String taskItemNo) {
		if (StringUtils.isEmpty(taskItemNo)) {
			return null;
		}

		return cb.like(root.get("taskItemNo").as(String.class), "%" + taskItemNo + "%");
	}

	public static Predicate taskCreateDatePredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Predicate predicate = range.getPredicate("createTime", root, cb);
		if (predicate != null) {
			Subquery<String> taskItemQuery = query.subquery(String.class);
			Root<TaskItem> taskItemRoot = taskItemQuery.from(TaskItem.class);
			taskItemQuery.select(taskItemRoot.get("taskItemNo").as(String.class));

			return root.get("taskItemNo").as(String.class).in(taskItemQuery.where(predicate));
		}

		return null;
	}

	public static List<Predicate> taskFinishDatePredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			DateRange range) {
		if (range == null) {
			return null;
		}

		Predicate predicate = range.getPredicate("endTime", root, cb);
		if (predicate == null) {
			return null;
		}

		List<Predicate> list = new ArrayList<>();
		Subquery<String> taskItemQuery = query.subquery(String.class);
		Root<TaskItem> taskItemRoot = taskItemQuery.from(TaskItem.class);
		taskItemQuery.select(taskItemRoot.get("taskItemNo").as(String.class));
		List<Predicate> predicateList = new ArrayList<>();
		predicateList.add(predicate);
		list.add(root.get("taskItemNo").as(String.class).in(taskItemQuery.where(predicate)));
		list.add(taskFinishPredicate(root, query, cb));
		return list;
	}

	private static Predicate taskFinishPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		Subquery<String> reviewRecordQuery = query.subquery(String.class);
		Root<ReviewRecord> reviewRecordRoot = reviewRecordQuery.from(ReviewRecord.class);
		reviewRecordQuery.select(reviewRecordRoot.get("taskItemNo").as(String.class));
		Predicate predicate = cb.equal(reviewRecordRoot.get("status").as(ReviewRecordStatus.class),
				ReviewRecordStatus.PASS);
		return root.get("taskItemNo").as(String.class).in(reviewRecordQuery.where(predicate));
	}

	public static Predicate taskViewTagIdsPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> viewTagItemIds) {
		if (viewTagItemIds.size() == 0) {
			return null;
		}

		Subquery<Long> taskItemXViewTagQuery = query.subquery(Long.class);
		Root<TaskItemXViewTag> taskItemXViewTagRoot = taskItemXViewTagQuery.from(TaskItemXViewTag.class);
		taskItemXViewTagQuery.select(taskItemXViewTagRoot.get("taskItemId").as(Long.class));

		Predicate predicate = taskItemXViewTagRoot.get("viewTagItemId").as(Long.class).in(viewTagItemIds);

		return root.get("taskItemId").as(Long.class).in(taskItemXViewTagQuery.where(predicate));
	}

	public static Predicate taskViewTagsPredicate(Root<TaskItem> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			Map<Long, ArrayList<Long>> viewTagMap) {
		if (viewTagMap == null || viewTagMap.isEmpty()) {
			return null;
		}

		Subquery<Long> taskItemXViewTagQuery = query.subquery(Long.class);
		List<Predicate> list = new ArrayList<>();
		for (Long viewTagId : viewTagMap.keySet()) {
			ArrayList<Long> viewTagItemIds = viewTagMap.get(viewTagId);
			Root<TaskItemXViewTag> taskItemXViewTagRoot = taskItemXViewTagQuery.from(TaskItemXViewTag.class);
			taskItemXViewTagQuery.select(taskItemXViewTagRoot.get("taskItemId").as(Long.class));
			//Predicate predicate1 =cb.equal(taskItemXViewTagRoot.get("viewTagId").as(Long.class),viewTagId);
			Predicate predicate2 = taskItemXViewTagRoot.get("viewTagItemId").as(Long.class).in(viewTagItemIds);
			list.add(predicate2);
		}

		return root.get("id").as(Long.class).in(taskItemXViewTagQuery.where(toPredicateArray(list)));
	}

	private static Predicate[] toPredicateArray(List<Predicate> list) {
		Predicate[] array = new Predicate[list.size()];
		list.toArray(array);
		return array;
	}
}

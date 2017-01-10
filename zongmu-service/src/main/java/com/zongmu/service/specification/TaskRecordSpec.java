package com.zongmu.service.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.zongmu.service.dto.search.Compare;
import com.zongmu.service.entity.TaskRecord;

public class TaskRecordSpec {

	public static Predicate pointPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, Compare point) {
		if (point == null) {
			return null;
		}

		Subquery<String> taskRecordQuery = query.subquery(String.class);
		Root<TaskRecord> taskRecordRoot = taskRecordQuery.from(TaskRecord.class);
		taskRecordQuery.select(taskRecordRoot.get("taskRecordNo").as(String.class));

		Predicate pointPredicate = point.getPredicate("point", taskRecordRoot, cb);
		if (pointPredicate == null) {
			return null;
		}

		return root.get("taskRecordNo").as(String.class).in(taskRecordQuery.where(pointPredicate));
	}
}

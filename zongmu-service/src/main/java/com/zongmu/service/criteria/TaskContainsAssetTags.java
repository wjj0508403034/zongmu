package com.zongmu.service.criteria;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;

import com.zongmu.service.entity.TaskRefAssetTag;

public class TaskContainsAssetTags implements Filter {
	private List<Long> tagIds = new ArrayList<>();

	public TaskContainsAssetTags(String tags) {
		if (tags.startsWith("[") && tags.endsWith("]")) {
			String[] parts = tags.substring(1, tags.length() - 1).split(",");
			for (String part : parts) {
				if (!StringUtils.isEmpty(part)) {
					this.tagIds.add(Long.parseLong(part));
				}

			}
		}
	}

	@Override
	public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		List<Predicate> list = new ArrayList<>();
		for (Long tagId : this.tagIds) {
			Subquery<TaskRefAssetTag> subQuery = query.subquery(TaskRefAssetTag.class);
			Root<TaskRefAssetTag> subRoot = subQuery.from(TaskRefAssetTag.class);
			subQuery.select(subRoot);
			Predicate eq1 = cb.equal(root.get("taskId").as(Long.class), subRoot.get("taskId").as(Long.class));
			Predicate eq2 = cb.equal(subRoot.get("assetTagId").as(Long.class), tagId);
			subQuery.where(eq1, eq2);
			Predicate exist = cb.exists(subQuery);
			list.add(exist);
		}

		if (list.size() != 0) {
			Predicate[] predicates = new Predicate[list.size()];
			return cb.and(list.toArray(predicates));
		}

		return null;

	}

}

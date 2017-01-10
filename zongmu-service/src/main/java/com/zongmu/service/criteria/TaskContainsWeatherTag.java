package com.zongmu.service.criteria;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;

import com.zongmu.service.entity.Task;

public class TaskContainsWeatherTag implements Filter {

	private List<Long> tagIds = new ArrayList<>();

	public TaskContainsWeatherTag(String tags) {
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
		if (this.tagIds.size() > 0) {
//			Subquery<Long> subQuery = query.subquery(Long.class);
//			Root<Task> subRoot = subQuery.from(Task.class);
//			subQuery.select(subRoot.get("id").as(Long.class));
//			subQuery.where(subRoot.get("weatherTagId").as(Long.class).in(tagIds));
//			return root.get("taskId").as(Long.class).in(subQuery);
			return root.get("weatherTagId").as(Long.class).in(tagIds);
		}

		return null;
	}

}

package com.zongmu.service.specification;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class AlgorithmSpec {

	public static Predicate algorithmPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			List<Long> algorithmIds) {
		if (algorithmIds.size() == 0) {
			return null;
		}

		return root.get("algorithmId").as(Long.class).in(algorithmIds);
	}
}

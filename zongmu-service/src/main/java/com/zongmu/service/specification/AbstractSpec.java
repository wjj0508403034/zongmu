package com.zongmu.service.specification;

import java.util.List;

import javax.persistence.criteria.Predicate;

public abstract class AbstractSpec {

	protected Predicate[] toPredicateArray(List<Predicate> list) {
		Predicate[] array = new Predicate[list.size()];
		list.toArray(array);
		return array;
	}
}

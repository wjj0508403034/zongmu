package com.zongmu.service.criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface Filter {

	Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb);
}

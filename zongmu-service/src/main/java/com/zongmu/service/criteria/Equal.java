package com.zongmu.service.criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class Equal extends Compare {

	private Class<?> type;

	public Equal(String name, Object value, Class<?> type) {
		super(name, value);
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	@Override
	public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return cb.equal(root.get(this.getName()).as(this.getType()), this.getValue());
	}
}

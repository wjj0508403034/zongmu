package com.zongmu.service.criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;

public class LessThan extends Compare {
	private Class<?> type;

	public LessThan(String name, Object value, Class<?> type) {
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
		if (this.getType() == Integer.class) {
			return cb.lessThan(root.get(this.getName()).as(Integer.class), (Integer) this.getValue());
		}

		if (this.getType() == int.class) {
			return cb.lessThan(root.get(this.getName()).as(int.class), (int) this.getValue());
		}

		if (this.getType() == DateTime.class) {
			return cb.lessThan(root.get(this.getName()).as(DateTime.class), (DateTime) this.getValue());
		}

		return null;
	}
}

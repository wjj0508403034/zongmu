package com.zongmu.service.criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

public class OrderBy {

	private String name;
	private boolean ascending = true;
	private Class<?> type;

	public OrderBy(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

	public OrderBy(String name, Class<?> type, boolean ascending) {
		this.name = name;
		this.type = type;
		this.ascending = ascending;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAscending(boolean asc) {
		this.ascending = asc;
	}

	public boolean isAscending() {
		return this.ascending;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Order toOrder(Root<?> root, CriteriaBuilder cb) {
		if (this.isAscending()) {
			return cb.asc(root.get(this.getName()).as(this.getType()));
		}

		return cb.desc(root.get(this.getName()).as(this.getType()));
	}

}

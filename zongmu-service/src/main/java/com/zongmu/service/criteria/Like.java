package com.zongmu.service.criteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class Like implements Filter {

	private String name;
	private String value;

	public Like(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return cb.like(root.get(this.getName()).as(String.class), "%" + this.getValue() + "%");
	}

	public static Like parse(String expr) {
		String[] parts = expr.split(" ");
		return new Like(parts[0], parts[2]);
	}
}

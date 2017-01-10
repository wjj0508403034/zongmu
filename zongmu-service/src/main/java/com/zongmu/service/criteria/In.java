package com.zongmu.service.criteria;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class In implements Filter {
	private List<Long> list = new ArrayList<>();
	private String name;

	public <T> In(String name, List<Long> list) {
		this.name = name;
		this.list = list;
	}

	@Override
	public Predicate toPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return root.get(this.name).as(Long.class).in(list);
	}
}

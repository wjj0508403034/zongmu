package com.zongmu.service.dto.search;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class Compare {

	private Op op;
	private Long value;

	public Op getOp() {
		return op;
	}

	public Long getValue() {
		return value;
	}

	public void setOp(Op op) {
		this.op = op;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public boolean isNull() {
		return value == null;
	}

	public Predicate getPredicate(String fieldName, Root<?> root, CriteriaBuilder cb) {
		if (this.getValue() == null) {
			return null;
		}

		if (this.getOp() == Op.LessThan) {
			return cb.lessThan(root.get(fieldName).as(int.class), this.getValue().intValue());
		}

		if (this.getOp() == Op.Equal) {
			return cb.equal(root.get(fieldName).as(int.class), this.getValue().intValue());
		}

		if (this.getOp() == Op.GreatThan) {
			return cb.greaterThan(root.get(fieldName).as(int.class), this.getValue().intValue());
		}

		return null;
	}

}

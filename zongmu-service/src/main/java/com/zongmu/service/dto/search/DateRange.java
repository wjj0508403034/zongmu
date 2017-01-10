package com.zongmu.service.dto.search;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;

public class DateRange {

	private DateTime from;
	private DateTime to;

	public DateTime getFrom() {
		return from;
	}

	public DateTime getTo() {
		return to;
	}

	public void setFrom(DateTime from) {
		this.from = from;
	}

	public void setTo(DateTime to) {
		this.to = to;
	}

	public boolean isNull() {
		return from == null && to == null;
	}

	public Predicate getPredicate(String fieldName, Root<?> root, CriteriaBuilder cb) {
		if (this.getFrom() != null) {
			if (this.getTo() != null) {
				return cb.between(root.get(fieldName).as(DateTime.class), this.getFrom(), this.getTo());
			}
			return cb.greaterThan(root.get(fieldName).as(DateTime.class), this.getFrom());
		}

		if (this.getTo() != null) {
			return cb.lessThan(root.get(fieldName).as(DateTime.class), this.getTo());
		}

		return null;
	}

}

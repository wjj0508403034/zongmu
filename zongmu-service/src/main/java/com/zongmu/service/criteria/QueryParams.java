package com.zongmu.service.criteria;

import java.util.ArrayList;
import java.util.List;

public class QueryParams {

	private List<Filter> filters = new ArrayList<>();

	private List<OrderBy> orders = new ArrayList<>();

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public List<OrderBy> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderBy> orders) {
		this.orders = orders;
	}

	public void add(Filter filter) {
		this.filters.add(filter);
	}

	public void add(OrderBy order) {
		this.orders.add(order);
	}

}

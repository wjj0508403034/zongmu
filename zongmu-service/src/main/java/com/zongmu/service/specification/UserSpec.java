package com.zongmu.service.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;

import com.zongmu.service.entity.User;

public class UserSpec {

	public static Predicate userPredicate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb, String userName) {
		if (StringUtils.isEmpty(userName)) {
			return null;
		}

		Subquery<Long> userQuery = query.subquery(Long.class);
		Root<User> userRoot = userQuery.from(User.class);
		userQuery.select(userRoot.get("Id").as(Long.class));

		Predicate like1 = cb.like(userRoot.get("userName").as(String.class), "%" + userName + "%");
		Predicate like2 = cb.like(userRoot.get("email").as(String.class), "%" + userName + "%");
		return root.get("userId").as(Long.class).in(userQuery.where(cb.or(like1, like2)));
	}
}

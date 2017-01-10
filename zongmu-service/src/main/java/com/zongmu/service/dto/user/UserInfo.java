package com.zongmu.service.dto.user;

import org.springframework.security.core.authority.AuthorityUtils;

import com.zongmu.service.entity.User;

public class UserInfo extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = -2421033024714502747L;
	private User user;

	public UserInfo(User user) {
		super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList("ROLE_" + user.getRole().toString()));
		this.setUser(user);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}

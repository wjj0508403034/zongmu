package com.zongmu.service.dto.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zongmu.service.entity.User;
import com.zongmu.service.internal.service.UserService;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = this.userService.getUser(email);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("User with email=%s was not found", email));
		}

		return new UserInfo(user);
	}

}

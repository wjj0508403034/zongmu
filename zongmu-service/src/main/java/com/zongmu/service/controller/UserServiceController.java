package com.zongmu.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.zongmu.service.dto.user.ActiveParam;
import com.zongmu.service.dto.user.ActiveResult;
import com.zongmu.service.dto.user.ForgetPasswordParam;
import com.zongmu.service.dto.user.ForgetResetPassword;
import com.zongmu.service.dto.user.LoginParam;
import com.zongmu.service.dto.user.MyProfile;
import com.zongmu.service.dto.user.ReactiveParam;
import com.zongmu.service.dto.user.RegisterParam;
import com.zongmu.service.dto.user.ResetPasswordParam;
import com.zongmu.service.dto.user.RoleParam;
import com.zongmu.service.dto.user.UserProfileParam;
import com.zongmu.service.entity.User;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.internal.service.UserService;

@Controller
@RequestMapping
public class UserServiceController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@ResponseBody
	public Page<User> getUserList(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
			@RequestParam(value = "role", required = false, defaultValue = "0") int role) {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.userService.getUserList(pageable, role);
	}

	@RequestMapping(value = "/black/users", method = RequestMethod.GET)
	@ResponseBody
	public Page<User> getBlackUserList(
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
		Pageable pageable = new PageRequest(pageIndex, pageSize);
		return this.userService.getBlackUserList(pageable);
	}
	
	@RequestMapping(value = "/black/{userId}/add", method = RequestMethod.POST)
	@ResponseBody
	public void addBlackList(@PathVariable("userId") Long userId) throws BusinessException{
		this.userService.addBlackList(userId);
	}
	
	@RequestMapping(value = "/black/{userId}/remove", method = RequestMethod.POST)
	@ResponseBody
	public void removeBlackList(@PathVariable("userId") Long userId) throws BusinessException{
		this.userService.removeBlackList(userId);
	}

	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public User getUser(@PathVariable("userId") Long userId) {
		return this.userService.getUserById(userId);
	}

	@RequestMapping(value = "/users/{userId}/role", method = RequestMethod.POST)
	@ResponseBody
	public void setUserRole(@PathVariable("userId") Long userId, @RequestBody RoleParam roleParam)
			throws BusinessException {
		this.userService.setUserRole(userId, roleParam.getRole());
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public void register(@RequestBody RegisterParam registerParam) throws BusinessException {
		this.userService.register(registerParam);
	}

	@RequestMapping(value = "/active", method = RequestMethod.POST)
	@ResponseBody
	public ActiveResult active(@RequestBody ActiveParam activeParam) throws BusinessException {
		return this.userService.active(activeParam);
	}

	@RequestMapping(value = "/reactive", method = RequestMethod.POST)
	@ResponseBody
	public void reactive(@RequestBody ReactiveParam reactiveParam) throws BusinessException {
		this.userService.reactive(reactiveParam);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public User login(@RequestBody LoginParam loginParam) throws BusinessException {
		return this.userService.login(loginParam);
	}
	
	@RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
	@ResponseBody
	public void forgetPassword(@RequestBody ForgetPasswordParam forgetPasswordParam) throws BusinessException {
		this.userService.forgetPassword(forgetPasswordParam);
	}

	@RequestMapping(value = "/forgetResetPassword", method = RequestMethod.POST)
	@ResponseBody
	public void forgetResetPassword(@RequestBody ForgetResetPassword forgetResetPassword) throws BusinessException {
		this.userService.forgetResetPassword(forgetResetPassword);
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public void resetPassword(@RequestBody ResetPasswordParam resetPasswordParam) throws BusinessException {
		this.userService.resetPassword(resetPasswordParam);
	}

	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	@ResponseBody
	public void updateUserProfile(@RequestBody UserProfileParam userProfileParam) throws BusinessException {
		this.userService.updateUserProfile(userProfileParam);
	}

	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	@ResponseBody
	public MyProfile myProfile() throws BusinessException {
		return this.userService.getMyProfile();
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@ResponseBody
	public void logout() {
		this.userService.logout();
	}

	@RequestMapping(value = "/uploadIcon", method = RequestMethod.POST)
	@ResponseBody
	public void uploadUserIcon(@RequestParam("file") MultipartFile file) {
		// this.userService.changeUserIcon(file);
	}

}

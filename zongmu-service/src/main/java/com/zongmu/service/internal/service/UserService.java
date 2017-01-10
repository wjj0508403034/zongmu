package com.zongmu.service.internal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.zongmu.service.dto.user.ActiveParam;
import com.zongmu.service.dto.user.ActiveResult;
import com.zongmu.service.dto.user.BusinessRole;
import com.zongmu.service.dto.user.ForgetPasswordParam;
import com.zongmu.service.dto.user.ForgetResetPassword;
import com.zongmu.service.dto.user.LoginParam;
import com.zongmu.service.dto.user.MyProfile;
import com.zongmu.service.dto.user.ReactiveParam;
import com.zongmu.service.dto.user.RegisterParam;
import com.zongmu.service.dto.user.ResetPasswordParam;
import com.zongmu.service.dto.user.UserProfileParam;
import com.zongmu.service.entity.User;
import com.zongmu.service.exception.BusinessException;

public interface UserService {

	void register(RegisterParam registerParam) throws BusinessException;

	User login(LoginParam loginParam) throws BusinessException;

	void forgetPassword(ForgetPasswordParam forgetPasswordParam) throws BusinessException;

	void resetPassword(ResetPasswordParam resetPasswordParam) throws BusinessException;

	void updateUserProfile(UserProfileParam userProfileParam);

	void logout();

	ActiveResult active(ActiveParam activeParam) throws BusinessException;

	void reactive(ReactiveParam reactiveParam) throws BusinessException;

	void forgetResetPassword(ForgetResetPassword forgetResetPassword) throws BusinessException;
	
	User getUser(String email);

	MyProfile getMyProfile();

	void changeUserIcon(MultipartFile file) throws BusinessException;
	
	User getUser(Long id);

	Page<User> getUserList(Pageable pageable, int role);
	
	Page<User> getBlackUserList(Pageable pageable);

	User getUserById(Long userId);

	void setUserRole(Long userId, BusinessRole role) throws BusinessException;

	void addBlackList(Long userId) throws BusinessException;

	void removeBlackList(Long userId) throws BusinessException;
	
	String getUserName(Long userId);
}

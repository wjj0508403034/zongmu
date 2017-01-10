package com.zongmu.service.internal.service.impl;

import java.util.UUID;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zongmu.service.configuration.BusinessObjectFacade;
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
import com.zongmu.service.email.EmailService;
import com.zongmu.service.entity.User;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.internal.service.UserService;
import com.zongmu.service.locale.LocaleService;
import com.zongmu.service.repository.UserRepository;
import com.zongmu.service.util.FileService;

@Service
public class UserServiceImpl implements UserService {

	private static Logger logger = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepos;

	@Autowired
	private EmailService emailService;

	@Autowired
	private LocaleService localeService;

	@Autowired
	private BusinessObjectFacade boFacade;

	@Autowired
	private FileService fileService;

	@Autowired
	@Qualifier("org.springframework.security.authenticationManager")
	private AuthenticationManager authenticationManager;

	@Override
	public void register(RegisterParam registerParam) throws BusinessException {
		logger.info("User " + registerParam.getEmail() + " start register ...");
		if (!this.emailAddressValid(registerParam.getEmail())) {
			throw new BusinessException(ErrorCode.EMAIL_FORMAT_INVALID);
		}

		if (!StringUtils.equals(registerParam.getPassword(), registerParam.getRepeatPassword())) {
			throw new BusinessException(ErrorCode.Password_NOT_MATCH);
		}

		boolean exist = this.userRepos.exists(registerParam.getEmail());
		if (exist) {
			throw new BusinessException(ErrorCode.USER_EXIST);
		}

		User user = new User();
		user.setEmail(registerParam.getEmail());
		user.setPassword(registerParam.getPassword());
		user.setActiveCode(UUID.randomUUID().toString());
		user.setRegisterDate(DateTime.now());
		user.setUpdateDate(DateTime.now());
		this.userRepos.save(user);

		this.emailService.sendRegisterMail(user);
	}

	@Override
	public void reactive(ReactiveParam reactiveParam) throws BusinessException {
		User user = this.userRepos.findByEmail(reactiveParam.getEmail());
		if (user == null) {
			throw new BusinessException(ErrorCode.ACTIVE_USER_NOT_EXIST);
		}

		user.setActiveCode(UUID.randomUUID().toString());
		user.setRegisterDate(DateTime.now());
		user.setUpdateDate(DateTime.now());
		this.userRepos.save(user);
		this.emailService.sendRegisterMail(user);
	}

	@Override
	public ActiveResult active(ActiveParam activeParam) {
		ActiveResult activeResult = new ActiveResult();
		User user = this.userRepos.findByEmail(activeParam.getEmail());
		if (user == null) {
			activeResult.setMessage(this.localeService.getErrorMessage(ErrorCode.ACTIVE_USER_NOT_EXIST));
			activeResult.setErrorCode(ErrorCode.ACTIVE_USER_NOT_EXIST);
			return activeResult;
		}

		if (user.isActive()) {
			activeResult.setMessage(this.localeService.getErrorMessage(ErrorCode.USER_IS_ACTIVE));
			activeResult.setErrorCode(ErrorCode.USER_IS_ACTIVE);
			return activeResult;
		}

		if (user.getRegisterDate().plusDays(1).isBeforeNow()) {
			activeResult.setErrorCode(ErrorCode.ACTIVE_DATA_IS_OVERDUE);
			activeResult.setMessage(this.localeService.getErrorMessage(ErrorCode.ACTIVE_DATA_IS_OVERDUE));
			return activeResult;
		}

		if (!StringUtils.equals(activeParam.getCode(), user.getActiveCode())) {
			activeResult.setErrorCode(ErrorCode.ACTIVE_CODE_INVALID);
			activeResult.setMessage(this.localeService.getErrorMessage(ErrorCode.ACTIVE_CODE_INVALID));
			return activeResult;
		}

		user.setActive(true);
		user.setUpdateDate(DateTime.now());
		this.userRepos.save(user);
		activeResult.setResult(true);
		return activeResult;
	}

	@Override
	public User login(LoginParam loginParam) throws BusinessException {
		User user = this.userRepos.findByEmail(loginParam.getEmail());
		if (user == null) {
			throw new BusinessException(ErrorCode.USER_NOT_EXIST);
		}

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.USER_IS_NOT_ACTIVE);
		}

		if (user.getBlack()) {
			throw new BusinessException(ErrorCode.LOGIN_USER_IS_BLACK);
		}

		if (user.isLocked()) {
			if (user.getLockedDate().plusHours(12).isAfter(DateTime.now())) {
				throw new BusinessException(ErrorCode.LOGIN_USER_IS_LOCKEZD);
			} else {
				user.setLocked(false);
				user.setLoginFailedCount(0);
				this.userRepos.save(user);
			}
		}

		if (!StringUtils.equals(user.getPassword(), loginParam.getPassword())) {
			user.setLoginFailedCount(user.getLoginFailedCount() + 1);
			if (user.getLoginFailedCount() >= 5) {
				user.setLocked(true);
				user.setLockedDate(DateTime.now());
			}
			this.userRepos.save(user);
		} else {
			user.setLocked(false);
			user.setLoginFailedCount(0);
			this.userRepos.save(user);
		}

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginParam.getEmail(),
				loginParam.getPassword());
		HttpServletRequest currentRequest = this.boFacade.currentRequest();
		token.setDetails(new WebAuthenticationDetails(currentRequest));
		Authentication auth = this.authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(auth);
		currentRequest.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				SecurityContextHolder.getContext());
		return user;
	}

	@Override
	public void forgetPassword(ForgetPasswordParam forgetPasswordParam) throws BusinessException {
		logger.info("User " + forgetPasswordParam.getEmail() + " start register ...");
		if (!this.emailAddressValid(forgetPasswordParam.getEmail())) {
			throw new BusinessException(ErrorCode.EMAIL_FORMAT_INVALID);
		}

		User user = this.userRepos.findByEmail(forgetPasswordParam.getEmail());
		if (user == null) {
			throw new BusinessException(ErrorCode.USER_NOT_EXIST);
		}

		user.setResetPasswordActiveCode(UUID.randomUUID().toString());
		user.setResetPasswordDate(DateTime.now());
		user.setResetActive(true);
		this.userRepos.save(user);

		this.emailService.sendForgetPasswordMail(user);
	}

	@Override
	public void forgetResetPassword(ForgetResetPassword forgetResetPassword) throws BusinessException {
		if (!StringUtils.equals(forgetResetPassword.getPassword(), forgetResetPassword.getRepeatPassword())) {
			throw new BusinessException(ErrorCode.RESET_PASSWORD_NOT_MATCH);
		}

		User user = this.userRepos.findByEmail(forgetResetPassword.getEmail());
		if (user == null) {
			throw new BusinessException(ErrorCode.USER_NOT_EXIST);
		}

		if (!user.isResetActive()) {
			throw new BusinessException(ErrorCode.RESET_NOT_ACTIVE);
		}

		if (user.getResetPasswordDate().plusDays(1).isBeforeNow()) {
			throw new BusinessException(ErrorCode.RESET_DATA_IS_OVERDUE);
		}

		if (!StringUtils.equalsIgnoreCase(forgetResetPassword.getCode(), user.getResetPasswordActiveCode())) {
			throw new BusinessException(ErrorCode.RESET_ACTIVE_CODE_INVALID);
		}

		user.setResetActive(false);
		user.setPassword(forgetResetPassword.getPassword());
		this.userRepos.save(user);
	}

	@Override
	public void resetPassword(ResetPasswordParam resetPasswordParam) throws BusinessException {
		if (StringUtils.equals(resetPasswordParam.getOldPassword(), resetPasswordParam.getPassword())) {
			throw new BusinessException(ErrorCode.OLD_PASSWORD_EQ_NEW_PASSWORD);
		}

		if (!StringUtils.equals(resetPasswordParam.getPassword(), resetPasswordParam.getRepeatPassword())) {
			throw new BusinessException(ErrorCode.Password_NOT_MATCH);
		}

		User user = this.boFacade.currentUser();

		if (!StringUtils.equals(user.getPassword(), resetPasswordParam.getOldPassword())) {
			throw new BusinessException(ErrorCode.RESET_PASSWORD_INVALID);
		}

		user.setPassword(resetPasswordParam.getPassword());
		this.userRepos.save(user);
	}

	@Override
	public void updateUserProfile(UserProfileParam userProfileParam) {
		User user = this.boFacade.currentUser();
		user.setUserName(userProfileParam.getUserName());
		user.setSex(userProfileParam.getSex());
		user.setPhone(userProfileParam.getPhone());
		user.setAlipayAccount(userProfileParam.getAlipayAccount());
		user.setQq(userProfileParam.getQq());
		user.setWechat(userProfileParam.getWechat());
		this.userRepos.save(user);
	}

	@Override
	public void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	private boolean emailAddressValid(String email) {
		try {
			new InternetAddress(email, true);
			return true;
		} catch (Exception ex) {
			ExceptionUtils.getStackTrace(ex);
		}

		return false;
	}

	@Override
	public User getUser(String email) {
		return this.userRepos.findByEmail(email);
	}

	@Override
	public MyProfile getMyProfile() {
		User user = this.boFacade.currentUser();
		user = this.userRepos.findOne(user.getId());
		MyProfile profile = new MyProfile();
		profile.setEmail(user.getEmail());
		profile.setPhone(user.getPhone());
		profile.setSex(user.getSex());
		profile.setUserName(user.getUserName());
		profile.setAlipayAccount(user.getAlipayAccount());
		profile.setQq(user.getQq());
		profile.setWechat(user.getWechat());
		return profile;
	}

	@Override
	public void changeUserIcon(MultipartFile file) throws BusinessException {
		User user = this.boFacade.currentUser();
		user.setIcon(this.fileService.saveUserIcon(file));
		this.userRepos.save(user);
	}

	@Override
	public User getUser(Long id) {
		return this.userRepos.findOne(id);
	}

	@Override
	public Page<User> getUserList(Pageable pageable, int role) {
		if (role == 1) {
			return this.userRepos.getList(BusinessRole.ADMIN, pageable);
		} else if (role == 2) {
			return this.userRepos.getList(BusinessRole.FINANCE, pageable);
		} else if (role == 3) {
			return this.userRepos.getList(BusinessRole.REVIEW, pageable);
		} else if (role == 4) {
			return this.userRepos.getList(BusinessRole.UPLOAD, pageable);
		} else if (role == 5) {
			return this.userRepos.getList(BusinessRole.NORMAL, pageable);
		} else if (role == 6) {
			return this.userRepos.getList(BusinessRole.SUPER, pageable);
		}
		return this.userRepos.getList(pageable);
	}

	@Override
	public Page<User> getBlackUserList(Pageable pageable) {
		return this.userRepos.getBlackList(pageable);
	}

	@Override
	public User getUserById(Long userId) {
		return this.getUser(userId);
	}

	@Override
	public void setUserRole(Long userId, BusinessRole role) throws BusinessException {
		User user = this.getUser(userId);
		if (user == null) {
			throw new BusinessException(ErrorCode.USER_NOT_EXIST);
		}

		user.setBusinessRole(role);
		this.userRepos.save(user);
	}

	@Override
	public void addBlackList(Long userId) throws BusinessException {
		User user = this.getUser(userId);
		if (user == null) {
			throw new BusinessException(ErrorCode.USER_NOT_EXIST);
		}

		user.setBlack(true);
		this.userRepos.save(user);
	}

	@Override
	public void removeBlackList(Long userId) throws BusinessException {
		User user = this.getUser(userId);
		if (user == null) {
			throw new BusinessException(ErrorCode.USER_NOT_EXIST);
		}

		user.setBlack(false);
		this.userRepos.save(user);
	}

	@Override
	public String getUserName(Long userId) {
		if (userId == null) {
			return null;
		}
		User user = this.userRepos.findOne(userId);
		if (user != null) {
			if (!StringUtils.isEmpty(user.getUserName())) {
				return user.getUserName();
			}

			return user.getEmail();
		}
		return null;
	}

}

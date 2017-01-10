package com.zongmu.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.joda.time.DateTime;

import com.zongmu.service.dto.user.BusinessRole;
import com.zongmu.service.dto.user.Role;
import com.zongmu.service.dto.user.Sex;

@Entity
@Table
public class User implements Serializable {

	private static final long serialVersionUID = 2947742459611268717L;

	@Id
	@SequenceGenerator(name = "USER_SEQUENCE", sequenceName = "USER_SEQUENCE")
	@GeneratedValue(generator = "USER_SEQUENCE", strategy = GenerationType.AUTO)
	private Long Id;

	private String userName;

	private String email;

	private String phone;

	private String password;

	private Role role = Role.USER;

	private boolean active;

	private String activeCode;

	private DateTime registerDate;

	private DateTime updateDate;

	private String resetPasswordActiveCode;

	private DateTime resetPasswordDate;

	private boolean resetActive;

	private Sex sex = Sex.UNKOWN;

	private BusinessRole businessRole = BusinessRole.NORMAL;

	private String alipayAccount;

	private String icon;
	
	private String qq;
	
	private String wechat;
	
	private boolean black;
	
	private boolean locked;
	
	private int loginFailedCount;
	
	private DateTime lockedDate;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getActiveCode() {
		return activeCode;
	}

	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}

	public DateTime getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(DateTime registerDate) {
		this.registerDate = registerDate;
	}

	public DateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(DateTime updateDate) {
		this.updateDate = updateDate;
	}

	public String getResetPasswordActiveCode() {
		return resetPasswordActiveCode;
	}

	public void setResetPasswordActiveCode(String resetPasswordActiveCode) {
		this.resetPasswordActiveCode = resetPasswordActiveCode;
	}

	public DateTime getResetPasswordDate() {
		return resetPasswordDate;
	}

	public void setResetPasswordDate(DateTime resetPasswordDate) {
		this.resetPasswordDate = resetPasswordDate;
	}

	public boolean isResetActive() {
		return resetActive;
	}

	public void setResetActive(boolean resetActive) {
		this.resetActive = resetActive;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public String getAlipayAccount() {
		return alipayAccount;
	}

	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public BusinessRole getBusinessRole() {
		return businessRole;
	}

	public void setBusinessRole(BusinessRole businessRole) {
		this.businessRole = businessRole;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public boolean getBlack() {
		return black;
	}

	public void setBlack(boolean black) {
		this.black = black;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public int getLoginFailedCount() {
		return loginFailedCount;
	}

	public void setLoginFailedCount(int loginFailedCount) {
		this.loginFailedCount = loginFailedCount;
	}

	public DateTime getLockedDate() {
		return lockedDate;
	}

	public void setLockedDate(DateTime lockedDate) {
		this.lockedDate = lockedDate;
	}
}

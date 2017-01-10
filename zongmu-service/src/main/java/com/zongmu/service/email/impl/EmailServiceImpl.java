package com.zongmu.service.email.impl;

import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.zongmu.service.configuration.ApplicationProperties;
import com.zongmu.service.email.EmailService;
import com.zongmu.service.email.MailProperties;
import com.zongmu.service.email.MailSubjects;
import com.zongmu.service.entity.User;
import com.zongmu.service.exception.BusinessException;
import com.zongmu.service.exception.ErrorCode;
import com.zongmu.service.locale.LocaleService;

@Service
public class EmailServiceImpl implements EmailService {

	private static Logger logger = Logger.getLogger(EmailServiceImpl.class);
	private final static int SOCKET_TIMEOUT_MS = 10000;
	private final static int MAIL_SMTP_TIMEOUT = 20000;

	@Autowired
	private MailProperties mailProperties;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private LocaleService localeService;

	@Autowired
	private ApplicationProperties applicationProperties;

	private String getHtmlContent(String mailName, Context context) {
		return this.templateEngine.process(mailName, context);
	}

	@Override
	public void sendRegisterMail(User user) throws BusinessException {
		Context context = new Context(Locale.CHINA);
		context.setVariable("user", user);
		context.setVariable("link", applicationProperties.getHost() + "page/user/active.html?mail="
				+ user.getEmail().replace("@", "____") + "&code=" + user.getActiveCode());
		boolean result = this.sending(user.getEmail(), this.localeService.getSubject(MailSubjects.User_Active),
				this.getHtmlContent("user.active", context));
		if (!result) {
			throw new BusinessException(ErrorCode.SEND_MAIL_FAILED);
		}
	}

	@Override
	public void sendForgetPasswordMail(User user) throws BusinessException {
		Context context = new Context(Locale.CHINA);
		context.setVariable("user", user);
		context.setVariable("link", applicationProperties.getHost() + "page/user/forget.reset.password.html?mail="
				+ user.getEmail().replace("@", "____") + "&code=" + user.getResetPasswordActiveCode());
		boolean result = this.sending(user.getEmail(), this.localeService.getSubject(MailSubjects.Reset_Password),
				this.getHtmlContent("reset.password", context));
		if (!result) {
			throw new BusinessException(ErrorCode.SEND_MAIL_FAILED);
		}
	}

	private boolean sending(String to, String subject, String body) {
		try {
			logger.info("Start to sending mail ...");
			JavaMailSenderImpl sender = this.createMailSender();
			MimeMessage message = sender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);
			helper.setSentDate(new Date());
			helper.setFrom(mailProperties.getFrom());
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);
			sender.send(message);
			logger.info("Sending mail successfully.");
			return true;
		} catch (Exception e) {
			logger.info("Sending mail failed.");
			logger.warn("Send mail failed, {}", e);
		}
		return false;
	}

	private JavaMailSenderImpl createMailSender() {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(mailProperties.getServer());
		sender.setPort(mailProperties.getPort());
		sender.setDefaultEncoding(EmailConstants.UTF_8);
		if (!StringUtils.isEmpty(mailProperties.getUserName()) && !StringUtils.isEmpty(mailProperties.getPassword())) {
			sender.setUsername(mailProperties.getUserName());
			sender.setPassword(mailProperties.getPassword());
		}

		Properties properties = new Properties();
		properties.setProperty(EmailConstants.MAIL_DEBUG, "true");
		// if (smtp.getSecurtiyLevel() == SecurtiyLevel.SSL) {
		// properties.setProperty(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE,
		// "false");
		// properties.setProperty(EmailConstants.MAIL_SMTP_SSL_ENABLE, "true");
		// } else if (smtp.getSecurtiyLevel() == SecurtiyLevel.STARTTLS) {
		// properties.setProperty(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE,
		// "true");
		// properties.setProperty(EmailConstants.MAIL_SMTP_SSL_ENABLE, "false");
		// } else {
		// properties.setProperty(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE,
		// "false");
		// properties.setProperty(EmailConstants.MAIL_SMTP_SSL_ENABLE, "false");
		// }

		properties.setProperty(EmailConstants.MAIL_SMTP_TIMEOUT, Integer.toString(MAIL_SMTP_TIMEOUT));
		properties.setProperty(EmailConstants.MAIL_SMTP_CONNECTIONTIMEOUT, Integer.toString(SOCKET_TIMEOUT_MS));

		sender.setJavaMailProperties(properties);

		return sender;
	}

}

package com.zongmu.service.exception;

import java.net.SocketException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.zongmu.service.locale.LocaleService;

import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger logger = Logger.getLogger(BusinessExceptionHandler.class);

    @Autowired
    private LocaleService localeService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> UnexpectedError(Exception exception) {
        logger.error(exception);
        logger.error(ExceptionUtils.getStackTrace(exception));
        return this.BusinessError(new BusinessException(ErrorCode.General_Error));
    }
    
    @ExceptionHandler(SocketException.class)
    public ResponseEntity<Object> socketExceptionError(SocketException exception) {
        logger.error(exception);
        logger.error(ExceptionUtils.getStackTrace(exception));
        return this.BusinessError(new BusinessException(ErrorCode.Socket_Connection_Error));
    }
    
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> UnexpectedError(BadCredentialsException exception) {
        logger.error(exception);
        logger.error(ExceptionUtils.getStackTrace(exception));
        return this.BusinessError(new BusinessException(ErrorCode.USER_PASSWORD_INVALID));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> BusinessError(BusinessException businessException) {
        businessException.setMessage(this.localeService.getErrorMessage(businessException.getCode()));
        logger.error(businessException);
        logger.error(ExceptionUtils.getStackTrace(businessException));
        return new ResponseEntity<Object>(new BusinessExceptionResponse(businessException), HttpStatus.BAD_REQUEST);
    }
}

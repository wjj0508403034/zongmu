package com.zongmu.ffmpeg.exception;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//import com.zongmu.ffmpeg.internal.service.LocaleService;

import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {

    private static Logger logger = Logger.getLogger(BusinessExceptionHandler.class);

    /*@Autowired
    private LocaleService localeService;*/

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> UnexpectedError(Exception exception) {
        logger.error(exception);
        logger.error(exception.getStackTrace());
        return this.BusinessError(new BusinessException(ErrorCode.General_Error));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> BusinessError(BusinessException businessException) {
        //businessException.setMessage(this.localeService.getMessage(businessException.getCode()));
        logger.error(businessException);
        logger.error(businessException.getStackTrace());
        return new ResponseEntity<Object>(new BusinessExceptionResponse(businessException), HttpStatus.BAD_REQUEST);
    }
}

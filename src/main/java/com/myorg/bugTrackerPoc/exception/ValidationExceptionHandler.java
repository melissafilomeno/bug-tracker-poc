package com.myorg.bugTrackerPoc.exception;

import com.myorg.bugTrackerPoc.openapi.server.model.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Exception Handler for Validation errors
 */
@ControllerAdvice
public class ValidationExceptionHandler {
    private final MessageSource messageSource;

    public ValidationExceptionHandler(@Autowired MessageSource messageSource){
        this.messageSource = messageSource;
    }

    @ExceptionHandler(value = {jakarta.validation.ConstraintViolationException.class})
    public ResponseEntity<Object> handleValidationError(WebRequest webRequest){
        Error error = new Error();
        error.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        error.setMessage(messageSource.getMessage(String.valueOf(HttpStatus.BAD_REQUEST.value()), null, webRequest.getLocale()));

        return new ResponseEntity<>(error, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

}
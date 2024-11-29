package com.myorg.bugTrackerPoc.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.WebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import com.myorg.bugTrackerPoc.openapi.server.model.Error;

import java.util.Map;
import java.util.HashMap;

/**
 * Exception Handler for Validation errors
 */
@ControllerAdvice
public class ValidationExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = {jakarta.validation.ConstraintViolationException.class})
    public ResponseEntity<Object> handleValidationError(Exception exception, WebRequest webRequest){
        Error error = new Error();
        error.setCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        error.setMessage(messageSource.getMessage(String.valueOf(HttpStatus.BAD_REQUEST.value()), null, webRequest.getLocale()));

        return new ResponseEntity<>(error, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

}
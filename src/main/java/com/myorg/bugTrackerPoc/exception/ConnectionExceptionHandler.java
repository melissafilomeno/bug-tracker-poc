package com.myorg.bugTrackerPoc.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Exception Handler for connection errors
 */
@ControllerAdvice
public class ConnectionExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = {org.springframework.transaction.TransactionException.class})
    public ResponseEntity<Object> handleConnectionError(Exception exception, WebRequest webRequest){
        RestErrorBody restErrorBody = new RestErrorBody(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                messageSource.getMessage(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), null, webRequest.getLocale()));
        return new ResponseEntity<>(restErrorBody, HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

}

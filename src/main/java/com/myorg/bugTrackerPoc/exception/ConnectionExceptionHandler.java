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
 * Exception Handler for connection errors
 */
@ControllerAdvice
public class ConnectionExceptionHandler {

    private final MessageSource messageSource;

    public ConnectionExceptionHandler(@Autowired MessageSource messageSource){
        this.messageSource = messageSource;
    }

    @ExceptionHandler(value = {org.springframework.transaction.TransactionException.class})
    public ResponseEntity<Object> handleConnectionError(WebRequest webRequest){
        Error error = new Error();
        error.setCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        error.setMessage(messageSource.getMessage(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                null, webRequest.getLocale()));
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

}
